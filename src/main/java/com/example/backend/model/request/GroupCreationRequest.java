package com.example.backend.model.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCreationRequest {
    private int userId;
    private String groupName;
    private String urlImage;
    private List<Integer> listUsers;
}
