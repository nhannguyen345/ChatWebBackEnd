package com.example.backend.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.Notification;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.FriendRequestDTO;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Notification> getListNotificationsByUserId(long userId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    public Notification createFriendRequest(FriendRequestDTO friendRequestDTO) {
        User user = userRepository.findById(friendRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findByEmail(friendRequestDTO.getEmailReceiver())
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        // Tạo thông báo cho người nhận yêu cầu kết bạn
        Notification notification = new Notification();
        notification.setReceiver(friend); // Người nhận thông báo là người được gửi yêu cầu kết bạn
        notification.setSender(user); // Người gửi thông báo là người gửi yêu cầu kết bạn
        notification.setContent(" has sent you a friend request. Invite message: "
                + friendRequestDTO.getInviteMessage());
        notification.setNotificationType(Notification.NotificationType.FRIEND_REQUEST); // Loại thông báo là yêu cầu kết
                                                                                        // bạn
        notification.setCreatedAt(new Date()); // Ghi lại thời điểm thông báo

        // Lưu thông báo vào bảng Notification
        return notificationRepository.save(notification);
    }

    public void updateReadStatusNotification(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));

        notificationRepository.markNotificationAsRead(user);
    }

    public void deleteFriendRequestNotification(int userId, Long notificationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new UsernameNotFoundException("Notification not found: " + notificationId));
        if (user.getUsername().equals(notification.getReceiver().getUsername())) {
            notificationRepository.deleteById(notificationId);
        } else {
            throw new RuntimeException("User does not have permission to perform this request!");
        }
    }

}
