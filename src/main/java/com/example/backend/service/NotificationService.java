package com.example.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.Notification;
import com.example.backend.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getListNotificationsByUserId(long userId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }
}
