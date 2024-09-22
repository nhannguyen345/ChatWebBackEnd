package com.example.backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDTO {
    private int userId;
    private String userName;
    private String emailReceiver;
    private String inviteMessage;
}
