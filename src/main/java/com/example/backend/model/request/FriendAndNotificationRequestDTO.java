package com.example.backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendAndNotificationRequestDTO {
    private int userId;
    private int friendId;
    private Long notificationId;
}
