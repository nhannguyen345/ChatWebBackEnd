package com.example.backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialInfoUpdateRequest {
    // private int userId;
    private String fbLink;
    private String instaLink;
    private String twitterLink;
}