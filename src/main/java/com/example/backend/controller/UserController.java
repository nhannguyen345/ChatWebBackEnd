package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.event.WebSocketEventListener;
import com.example.backend.model.entity.GroupMember;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.AuthRequest;
import com.example.backend.model.request.PasswordUpdateRequest;
import com.example.backend.model.request.PersonalInfoUpdateRequest;
import com.example.backend.model.request.SocialInfoUpdateRequest;
import com.example.backend.model.response.AuthResponse;
import com.example.backend.service.GroupService;
import com.example.backend.service.JwtService;
import com.example.backend.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private WebSocketEventListener eventListener;

    @Autowired
    private UserService service;

    @Autowired
    private GroupService groupService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String addnewuser(@RequestBody User user) {
        return service.addUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateAndgetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            User userLogin = service.getUserByEmail(authRequest.getEmail());

            // Get all group which user in
            List<GroupMember> list = groupService.getListGroupsForUser(userLogin);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new AuthResponse(userLogin,
                            jwtService.generateToken(userLogin.getId(), userLogin.getUsername(),
                                    userLogin.getEmail()),
                            list));

        } else {
            throw new UsernameNotFoundException("Invalid email request!");
        }
    }

    @MessageMapping("/send-online-signal")
    public void getListUsersOnline() {
        simpMessagingTemplate.convertAndSend("/topic/getUsersOnline", eventListener.getListUsersOnline());
    }

    @PostMapping("/update-personal-info")
    public ResponseEntity<?> updatePersonalInfo(@RequestBody PersonalInfoUpdateRequest personalInfoUpdateRequest) {
        try {
            int updatedCount = service.updateUserPersonalInfo(personalInfoUpdateRequest);
            return ResponseEntity.ok().body(updatedCount);
        } catch (Exception e) {
            log.info("Error in controller user - updatePersonalInfo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/update-social-info")
    public ResponseEntity<?> updateSocialInfo(@RequestBody SocialInfoUpdateRequest socialInfoUpdateRequest) {
        try {
            int updatedCount = service.updateUserSocialInfo(socialInfoUpdateRequest);
            return ResponseEntity.ok().body(updatedCount);
        } catch (Exception e) {
            log.info("Error in controller user - updateSocialInfo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        try {
            int updatedCount = service.updateUserPassword(passwordUpdateRequest);
            return ResponseEntity.ok().body(updatedCount);
        } catch (Exception e) {
            log.info("Error in controller user - updatePassword: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
