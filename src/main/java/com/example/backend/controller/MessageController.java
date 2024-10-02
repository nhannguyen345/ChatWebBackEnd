package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.Message;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.NewMessageRequest;
import com.example.backend.model.response.Conversation;
import com.example.backend.service.MessageService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @PostMapping("/get-list-messages")
    public ResponseEntity<?> getListMessagesForUser(@RequestParam int userId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        log.info("username who loging: {}", username);
        try {
            List<Conversation> conversations = messageService.getAllMessagesForUserAndSorted(userId);
            return ResponseEntity.status(HttpStatus.OK).body(conversations);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }

    }

    @MessageMapping("/add-new-message")
    public void addNewMessage(@Payload NewMessageRequest newMessageRequest) {
        try {
            Message message = messageService.addNewMessage(newMessageRequest);
            simpMessagingTemplate.convertAndSendToUser(message.getReceiver().getUsername(), "/queue/newMessage",
                    message);
        } catch (Exception e) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            simpMessagingTemplate.convertAndSendToUser(username, "/queue/errors", e.getMessage());
        }
    }

}
