package com.example.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.request.FriendRequestDTO;
import com.example.backend.service.NotificationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RequestMapping("/notification")
@RestController
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/create-friend-request")
    public void createNewFriendRequest(@Payload FriendRequestDTO friendRequestDTO) {
        try {
            Notification notification = notificationService.createFriendRequest(friendRequestDTO);
            System.out.println(notification);
            simpMessagingTemplate.convertAndSendToUser(notification.getReceiver().getUsername(),
                    "/queue/notification",
                    notification);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            simpMessagingTemplate.convertAndSendToUser(friendRequestDTO.getUserName(),
                    "/queue/errors", e.getMessage());
        }
    }

    @GetMapping("/get-list-notifications/{id}")
    public ResponseEntity<?> getListNotifications(@PathVariable Long id) {
        try {
            List<Notification> notifications = notificationService.getListNotificationsByUserId(id);

            return ResponseEntity.status(HttpStatus.OK).body(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request.");
        }
    }

    @PutMapping("/updateReadStatus/{id}")
    public void updateReadStatusNotification(@PathVariable int id) {
        notificationService.updateReadStatusNotification(id);
    }

    @DeleteMapping("/delete-notification/{id}")
    public void deleteFriendRequestNotification(@PathVariable Long id) {
        notificationService.deleteFriendRequestNotification(id);
    }

}
