package com.example.backend.controller;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.Message;
import com.example.backend.model.request.NewMessageRequest;
import com.example.backend.model.response.Conversation;
import com.example.backend.model.response.SendMessageSuccess;
import com.example.backend.service.MessageService;
import com.example.backend.service.UserDetailsCustom;
import com.example.backend.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping("/get-list-messages")
    public ResponseEntity<?> getListMessagesForUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                log.info("Info of user in token: {}", uDetailsCustom);
                List<Conversation> conversations = messageService
                        .getAllMessagesForUserAndSorted(uDetailsCustom.getId());
                return ResponseEntity.status(HttpStatus.OK).body(conversations);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
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
            log.info("Thông tin message: {}", message);
            if (newMessageRequest.getReceiverId() != null) {
                simpMessagingTemplate.convertAndSendToUser(message.getReceiver().getUsername(), "/queue/new-message",
                        message);
            } else if (newMessageRequest.getGroupId() != null) {
                Instant createdAtInstant = message.getGroup().getCreatedAt().toInstant();
                String formattedCreatedAt = DateTimeFormatter.ISO_INSTANT.format(createdAtInstant);
                log.info("Time of group: {}", formattedCreatedAt);
                simpMessagingTemplate.convertAndSend("/topic/group/" + message.getGroup().getId() + "_"
                        + message.getGroup().getName() + "_" + formattedCreatedAt, message);
            }

            simpMessagingTemplate.convertAndSendToUser(message.getSender().getUsername(), "/queue/send-mess-success",
                    new SendMessageSuccess(newMessageRequest.getTempId(), message, "success"));
        } catch (Exception e) {
            log.error("Lỗi xuất hiện: ", e);
            String senderUsername = userService.getUserById(newMessageRequest.getSenderId()).getUsername();
            simpMessagingTemplate.convertAndSendToUser(senderUsername, "/queue/send-mess-error",
                    newMessageRequest.getTempId());
        }
    }

}
