package com.example.backend.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.FriendAndNotificationRequestDTO;

public interface FriendService {

        public Notification addNewFriendAndNotification(
                        FriendAndNotificationRequestDTO friendAndNotificationRequestDTO);

        public List<Map<String, Object>> sortedContacts(int userId);

        public Notification unFriendForUser(int userId1, int userId2);

        public CompletableFuture<Void> deleteAllMessageForTwoUsers(User user1, User user2);

}
