package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.request.FriendRequestDTO;
import com.example.backend.service.FriendService;
import com.example.backend.service.NotificationService;

@RestController
public class FriendController {

}
