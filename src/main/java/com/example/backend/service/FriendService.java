package com.example.backend.service;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.entity.Friend;
import com.example.backend.model.entity.Notification;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.FriendAndNotificationRequestDTO;
import com.example.backend.repository.FriendRepository;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FriendService {
        @Autowired
        private FriendRepository friendRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private MessageRepository messageRepository;

        @Autowired
        private NotificationRepository notificationRepository;

        public Notification addNewFriendAndNotification(
                        FriendAndNotificationRequestDTO friendAndNotificationRequestDTO) {

                User user = userRepository.findById(friendAndNotificationRequestDTO.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                User userRequest = userRepository.findById(friendAndNotificationRequestDTO.getFriendId())
                                .orElseThrow(() -> new RuntimeException("Friend not found"));

                // Add to database
                Friend friend1 = new Friend();
                friend1.setUser(user);
                friend1.setFriend(userRequest);
                friendRepository.save(friend1);

                Friend friend2 = new Friend();
                friend2.setUser(userRequest);
                friend2.setFriend(user);
                friendRepository.save(friend2);

                // Set disable for notification friend request
                notificationRepository.setNotificationDisable(friendAndNotificationRequestDTO.getNotificationId());

                Notification notification = new Notification();
                notification.setReceiver(userRequest);
                notification.setSender(user);
                notification.setContent(" has accepted your friend request.");
                notification.setNotificationType(Notification.NotificationType.FRIEND_REQUEST_ACCEPTED);

                notification.setCreatedAt(new Date());

                return notificationRepository.save(notification);
        }

        public List<Map<String, Object>> sortedContacts(int userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                List<Friend> list = friendRepository.findAllFriendsForUser(user);

                List<Map<String, Object>> listcontacts = list.stream()
                                .sorted(Comparator.comparing(fri -> fri.getFriend().getFirstLetter()))
                                .map(friend -> {
                                        Map<String, Object> contact = new HashMap<>();
                                        contact.put("letter", friend.getFriend().getFirstLetter());
                                        contact.put("id", friend.getFriend().getId());
                                        contact.put("username", friend.getFriend().getUsername());
                                        contact.put("image", friend.getFriend().getAvatarUrl());

                                        return contact;
                                })
                                .collect(Collectors.toList());
                return listcontacts;
        }

        @Transactional
        public Notification unFriendForUser(int userId1, int userId2) {
                User user1 = userRepository.findById(userId1)
                                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId1));

                User user2 = userRepository.findById(userId2)
                                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId2));

                int deletedRows = friendRepository.deleteFriendByUser(user1, user2);

                if (deletedRows > 0) {
                        CompletableFuture<Void> future = deleteAllMessageForTwoUsers(user1, user2);
                        future.exceptionally(e -> {
                                log.info("Error at addNewGroup - GroupService: {}", e.getMessage());
                                return null;
                        });

                        Notification notification = new Notification();
                        notification.setReceiver(user2);
                        notification.setSender(user1);
                        notification.setContent(" has unfriend you.");
                        notification.setNotificationType(Notification.NotificationType.UNFRIEND);

                        notification.setCreatedAt(new Date());

                        return notificationRepository.save(notification);
                } else {
                        throw new IllegalArgumentException("Two users provided are invalid!");
                }
        }

        @Async
        CompletableFuture<Void> deleteAllMessageForTwoUsers(User user1, User user2) {
                try {
                        messageRepository.deleteMessagesOfTwoUsers(user1, user2);
                        return CompletableFuture.completedFuture(null);
                } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return CompletableFuture.failedFuture(e);
                }

        }

}
