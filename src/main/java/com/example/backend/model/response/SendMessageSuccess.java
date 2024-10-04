package com.example.backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageSuccess {
    private String tempId;
    private Long newMessageId;
    private String status;
}
