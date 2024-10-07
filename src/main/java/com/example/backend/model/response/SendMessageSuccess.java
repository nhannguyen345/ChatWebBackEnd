package com.example.backend.model.response;

import com.example.backend.model.entity.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageSuccess {
    private String tempId;
    private Message newMessage;
    private String statusMess;
}
