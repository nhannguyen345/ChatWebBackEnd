package com.example.backend.model.response;

import com.example.backend.model.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private User info;
    private String jwtToken;
    private boolean success;
    private String message;
}
