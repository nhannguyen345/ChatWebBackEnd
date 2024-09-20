package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

}
