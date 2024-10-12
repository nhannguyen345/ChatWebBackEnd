package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.request.CallUserRequest;

@RestController
@RequestMapping("/call")
public class CallController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/call-user")
    public void CallUser(@Payload CallUserRequest callUserRequest) {
        simpMessagingTemplate.convertAndSendToUser(callUserRequest.getToUsername(), "/queue/receive-call",
                callUserRequest);
    }

    @MessageMapping("/answer-call")
    public void AnswerCall(@Payload CallUserRequest callUserRequest) {
        simpMessagingTemplate.convertAndSendToUser(callUserRequest.getFromUsername(), "/queue/answer-call",
                callUserRequest);
    }
}
