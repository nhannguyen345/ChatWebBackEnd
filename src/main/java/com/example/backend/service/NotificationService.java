package com.example.backend.service;

import java.util.List;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.request.FriendRequestDTO;

public interface NotificationService {

    public List<Notification> getListNotificationsByUserId(long userId);

    public Notification createFriendRequest(FriendRequestDTO friendRequestDTO);

    public void updateReadStatusNotification(int id);

    public void deleteFriendRequestNotification(int userId, Long notificationId);

    public void deleteAllNotificationsForUser(int userId);

}
