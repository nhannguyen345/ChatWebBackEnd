package com.example.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.request.FriendAndNotificationRequestDTO;
import com.example.backend.service.FriendService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
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

    @GetMapping("/get-contacts-list/{id}")
    public ResponseEntity<?> getListContactsForUser(@PathVariable int id) {
        try {
            List<Map<String, Object>> listContacts = friendService.sortedContacts(id);
            return ResponseEntity.ok().body(listContacts);
        } catch (Exception e) {
            log.info("Error at controller friend - getListContactsForUser: ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

}
