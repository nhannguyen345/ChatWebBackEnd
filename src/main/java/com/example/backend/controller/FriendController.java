package com.example.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.request.FriendAndNotificationRequestDTO;
import com.example.backend.service.FriendService;
import com.example.backend.service.UserDetailsCustom;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RestController
@RequestMapping("/friend")
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

    @GetMapping("/get-contacts-list")
    public ResponseEntity<?> getListContactsForUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                List<Map<String, Object>> listContacts = friendService.sortedContacts(uDetailsCustom.getId());
                return ResponseEntity.ok().body(listContacts);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
        } catch (Exception e) {
            log.info("Error at controller friend - getListContactsForUser: ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/unfriend/{userId}")
    public ResponseEntity<?> deleteFriendForUser(@PathVariable int userId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                Notification notification = friendService.unFriendForUser(uDetailsCustom.getId(), userId);
                simpMessagingTemplate.convertAndSendToUser(notification.getReceiver().getUsername(),
                        "/queue/notification", notification);
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
