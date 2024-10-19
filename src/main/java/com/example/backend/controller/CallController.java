package com.example.backend.controller;

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

import com.example.backend.model.entity.Call;
import com.example.backend.model.request.CallUserRequest;
import com.example.backend.model.request.NewCallRequest;
import com.example.backend.service.CallService;
import com.example.backend.service.UserDetailsCustom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequestMapping("/call")
public class CallController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private CallService callService;

    @MessageMapping("/call-user")
    public void CallUser(@Payload CallUserRequest callUserRequest) {
        simpMessagingTemplate.convertAndSendToUser(callUserRequest.getToUsername(), "/queue/receive-call",
                callUserRequest);
    }

    @MessageMapping("/answer-call")
    public void AnswerCall(@Payload CallUserRequest callUserRequest) {
        simpMessagingTemplate.convertAndSendToUser(callUserRequest.getFromUsername(), "/queue/accept-call",
                callUserRequest);
    }

    @MessageMapping("/call-decline")
    public void DeclineCall(String userNameCaller) {
        simpMessagingTemplate.convertAndSendToUser(userNameCaller, "/queue/call-declined",
                "The user on the other end declined the call!");
    }

    @MessageMapping("/call-end")
    public void EndCall(String userNameCaller) {
        simpMessagingTemplate.convertAndSendToUser(userNameCaller,
                "/queue/ended-call",
                "End call!");
    }

    @PostMapping("/add-new-call")
    public ResponseEntity<?> addNewCall(@RequestBody NewCallRequest newCallRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                log.info("Info of user in token: {}", uDetailsCustom);
                Call call = callService
                        .addNewCall(newCallRequest, uDetailsCustom.getId());
                return ResponseEntity.status(HttpStatus.OK).body(call);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e);
        }
    }

    @GetMapping("/get-list-calls")
    public ResponseEntity<?> getListCallForUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                log.info("Info of user in token: {}", uDetailsCustom);
                List<Call> calls = callService
                        .getListCall(uDetailsCustom.getId());
                return ResponseEntity.status(HttpStatus.OK).body(calls);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e);
        }
    }

}
