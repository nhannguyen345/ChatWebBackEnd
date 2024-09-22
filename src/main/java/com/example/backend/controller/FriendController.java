package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.request.FriendRequestDTO;
import com.example.backend.service.FriendService;

@RestController
public class FriendController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private FriendService friendService;

    @MessageMapping("/create-friend-request")
    public void createNewFriendRequest(@Payload FriendRequestDTO friendRequestDTO) {
        try {
            Notification notification = friendService.createFriendRequest(friendRequestDTO);
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

}
