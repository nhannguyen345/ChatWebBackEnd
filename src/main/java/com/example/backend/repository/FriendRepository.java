package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Integer> {

}
