package com.example.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.request.FriendRequestDTO;
import com.example.backend.service.NotificationService;
import com.example.backend.service.UserDetailsCustom;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
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

    @GetMapping("/get-list-notifications")
    public ResponseEntity<?> getListNotifications() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                log.info("Info of user in token: {}", uDetailsCustom);
                List<Notification> notifications = notificationService
                        .getListNotificationsByUserId(uDetailsCustom.getId());
                return ResponseEntity.status(HttpStatus.OK).body(notifications);
            } else {
                throw new RuntimeException("Token contains invalid information!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request.");
        }
    }

    @PutMapping("/updateReadStatus")
    public void updateReadStatusNotification() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                log.info("Info of user in token: {}", uDetailsCustom);
                notificationService.updateReadStatusNotification(uDetailsCustom.getId());
            } else {
                throw new RuntimeException("Token contains invalid information!");
            }
        } catch (Exception e) {
            log.info("error at controller - updateReadStatusNotification: {}", e.getMessage());
        }
    }

    @DeleteMapping("/delete-notification/{id}")
    public void deleteFriendRequestNotification(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                log.info("Info of user in token: {}", uDetailsCustom);
                notificationService.deleteFriendRequestNotification(uDetailsCustom.getId(), id);
            } else {
                throw new RuntimeException("Token contains invalid information!");
            }
        } catch (Exception e) {

        }
    }

}
