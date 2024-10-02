package com.example.backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewMessageRequest {
    private int senderId;
    private Integer receiverId;
    private Long groupId;
    private String content;
    private String fileUrl;
    private String messageType;
}
