package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.request.FriendAndNotificationRequestDTO;
import com.example.backend.model.request.FriendRequestDTO;
import com.example.backend.service.FriendService;
import com.example.backend.service.NotificationService;

@RestController
public class FriendController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private FriendService friendService;

    @MessageMapping("/add-new-friend")
    public void addNewFriend(@Payload FriendAndNotificationRequestDTO friendAndNotificationRequestDTO) {
        try {
            Notification notification = friendService.addNewFriendAndNotification(friendAndNotificationRequestDTO);
            simpMessagingTemplate.convertAndSendToUser(notification.getReceiver().getUsername(),
                    "/queue/notification",
                    notification);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            simpMessagingTemplate.convertAndSendToUser(friendAndNotificationRequestDTO.getUserName(),
                    "/queue/errors", e.getMessage());
        }
    }
}
