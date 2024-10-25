package com.example.backend.model.response;

import java.util.List;

import com.example.backend.model.entity.GroupMember;
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
    private List<GroupMember> groupMembers;
}
