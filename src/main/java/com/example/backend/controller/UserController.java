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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.event.WebSocketEventListener;
import com.example.backend.model.entity.GroupMember;
import com.example.backend.model.entity.PasswordResetToken;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.AuthRequest;
import com.example.backend.model.request.PasswordUpdateRequest;
import com.example.backend.model.request.PersonalInfoUpdateRequest;
import com.example.backend.model.request.SocialInfoUpdateRequest;
import com.example.backend.model.response.AuthResponse;
import com.example.backend.service.GroupService;
import com.example.backend.service.JwtService;
import com.example.backend.service.UserDetailsCustom;
import com.example.backend.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @PutMapping("/update-personal-info")
    public ResponseEntity<?> updatePersonalInfo(@RequestBody PersonalInfoUpdateRequest personalInfoUpdateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                int updatedCount = service.updateUserPersonalInfo(uDetailsCustom.getId(), personalInfoUpdateRequest);
                return ResponseEntity.ok().body(updatedCount);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
        } catch (Exception e) {
            log.info("Error in controller user - updatePersonalInfo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update-social-info")
    public ResponseEntity<?> updateSocialInfo(@RequestBody SocialInfoUpdateRequest socialInfoUpdateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                int updatedCount = service.updateUserSocialInfo(uDetailsCustom.getId(), socialInfoUpdateRequest);
                return ResponseEntity.ok().body(updatedCount);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
        } catch (Exception e) {
            log.info("Error in controller user - updateSocialInfo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                int updatedCount = service.updateUserPassword(uDetailsCustom.getId(), passwordUpdateRequest);
                return ResponseEntity.ok().body(updatedCount);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
        } catch (Exception e) {
            log.info("Error in controller user - updatePassword: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update-avatar")
    public ResponseEntity<?> updateAvatarUrl(@RequestParam String avatarUrl) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                int updatedCount = service.updateUserAvatar(uDetailsCustom.getId(), avatarUrl);
                return ResponseEntity.ok().body(updatedCount);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
        } catch (Exception e) {
            log.info("Error in controller user - updatePassword: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> checkEmailAndSendResetLink(@RequestBody String email) {
        try {
            return ResponseEntity.ok().body(service.checkEmailAndSendResetLinkToUser(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check-token/{token}")
    public ResponseEntity<?> checkTokenFromUser(@PathVariable String token) {
        try {
            return ResponseEntity.ok().body(service.checkToken(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/change-pass/{token}")
    public ResponseEntity<?> putMethodName(@PathVariable String token,
            @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        try {
            PasswordResetToken passwordResetToken = service.checkToken(token);
            int updatedCount = service.updateUserPassword(passwordResetToken.getUser().getId(), passwordUpdateRequest);
            service.updateStatusToken(token);
            return ResponseEntity.ok().body(updatedCount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
