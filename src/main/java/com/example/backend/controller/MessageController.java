package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.response.Conversation;
import com.example.backend.service.MessageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/mesage")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/get-list-messages")
    public ResponseEntity<?> getListMessagesForUser(@RequestBody int userId) {
        try {
            List<Conversation> conversations = messageService.getAllMessagesForUserAndSorted(userId);
            return ResponseEntity.status(HttpStatus.OK).body(conversations);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e);
        }

    }

}
