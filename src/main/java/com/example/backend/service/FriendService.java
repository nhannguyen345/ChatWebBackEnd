package com.example.backend.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.Friend;
import com.example.backend.model.entity.Notification;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.FriendRequestDTO;
import com.example.backend.repository.FriendRepository;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;

@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createFriendRequest(FriendRequestDTO friendRequestDTO) {
        User user = userRepository.findById(friendRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findByEmail(friendRequestDTO.getEmailReceiver())
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        Friend friendRequest = new Friend();
        friendRequest.setUser(user);
        friendRequest.setFriend(friend);
        friendRequest.setStatus(Friend.Status.PENDING); // Trạng thái mặc định là PENDING
        friendRequest.setCreatedAt(new Date()); // Ghi lại thời điểm tạo

        // Lưu yêu cầu kết bạn
        friendRepository.save(friendRequest);

        // Tạo thông báo cho người nhận yêu cầu kết bạn
        Notification notification = new Notification();
        notification.setReceiver(friend); // Người nhận thông báo là người được gửi yêu cầu kết bạn
        notification.setSender(user); // Người gửi thông báo là người gửi yêu cầu kết bạn
        notification.setContent(user.getUsername() + " has sent you a friend request.");
        notification.setNotificationType(Notification.NotificationType.FRIEND_REQUEST); // Loại thông báo là yêu cầu kết
                                                                                        // bạn
        notification.setCreatedAt(new Date()); // Ghi lại thời điểm thông báo

        // Lưu thông báo vào bảng Notification
        return notificationRepository.save(notification);
    }
}
