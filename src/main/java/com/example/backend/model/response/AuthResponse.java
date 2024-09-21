package com.example.backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private int id;
    private String username;
    private String jwtToken;
    private boolean success;
    private String message;
}
