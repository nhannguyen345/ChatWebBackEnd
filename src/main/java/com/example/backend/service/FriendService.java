package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.repository.FriendRepository;
import com.example.backend.repository.UserRepository;

@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    // public void createFriendRequest()
}
