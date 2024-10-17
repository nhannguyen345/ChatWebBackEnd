package com.example.backend.model.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCallRequest {
    private String receiverUsername;
    private String callStatus;
    private Date startedAt;
    private Date endedAt;
}
