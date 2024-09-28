package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.entity.Friend;
import com.example.backend.model.entity.User;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Modifying
    @Transactional
    @Query("SELECT f FROM Friend f WHERE f.user = :user OR f.friend = :user")
    List<Friend> findAllFriendsForUser(@Param("user") User user);
}
