package com.example.backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallUserRequest {
    private String fromUsername;
    private String toUsername;
    private Object signalData;
}
