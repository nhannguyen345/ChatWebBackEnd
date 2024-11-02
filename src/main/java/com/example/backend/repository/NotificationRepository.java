package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.receiver = :receiver AND n.read = false")
    int markNotificationAsRead(User receiver);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.disable = true WHERE n.id = :notificationId")
    int setNotificationDisable(Long notificationId);

    @Modifying
    @Transactional
    @Query("DELETE from Notification n WHERE n.receiver = :user")
    int deleteAllNotificationForUser(@Param("user") User user);
}
