package com.example.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.Notification;
import com.example.backend.service.NotificationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/notification")
@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/get-list-notifications/{id}")
    public ResponseEntity<?> getMethodName(@PathVariable Long id) {
        try {
            List<Notification> notifications = notificationService.getListNotificationsByUserId(id);
            return ResponseEntity.status(HttpStatus.OK).body(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request.");
        }
    }

}
