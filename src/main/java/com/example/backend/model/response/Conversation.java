package com.example.backend.model.response;

import java.util.Date;
import java.util.List;

import com.example.backend.model.entity.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    private String type;
    private Object entity;
    private Date lastMessageTime;
    private List<Message> messages;
}
