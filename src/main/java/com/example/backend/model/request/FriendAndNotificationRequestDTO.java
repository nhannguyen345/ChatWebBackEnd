package com.example.backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendAndNotificationRequestDTO {
    private int userId;
    private String userName;
    private int friendId;
    private String friendName;
    private Long notificationId;
}
