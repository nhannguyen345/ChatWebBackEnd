package com.example.backend.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.Friend;
import com.example.backend.model.entity.Notification;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.FriendAndNotificationRequestDTO;
import com.example.backend.repository.FriendRepository;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;

@Service
public class FriendService {
        @Autowired
        private FriendRepository friendRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private NotificationRepository notificationRepository;

        public Notification addNewFriendAndNotification(
                        FriendAndNotificationRequestDTO friendAndNotificationRequestDTO) {

                User user = userRepository.findById(friendAndNotificationRequestDTO.getUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                User userRequest = userRepository.findById(friendAndNotificationRequestDTO.getFriendId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Add to database
                Friend friend1 = new Friend();
                friend1.setUser(user);
                friend1.setFriend(userRequest);
                friendRepository.save(friend1);

                Friend friend2 = new Friend();
                friend2.setUser(userRequest);
                friend2.setFriend(user);
                friendRepository.save(friend2);

                Notification notification = new Notification();
                notification.setReceiver(userRequest);
                notification.setSender(user);
                notification.setContent(user.getUsername() + " has accepted your friend request.");
                notification.setNotificationType(Notification.NotificationType.FRIEND_REQUEST_ACCEPTED);

                notification.setCreatedAt(new Date());

                return notificationRepository.save(notification);
        }

}
