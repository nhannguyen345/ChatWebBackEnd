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
    @Query("SELECT f FROM Friend f WHERE f.user = :user")
    // @Query("SELECT f FROM Friend f WHERE f.user = :user OR f.friend = :user")
    List<Friend> findAllFriendsForUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Friend f WHERE (f.user = :user1 AND f.friend = :user2) OR (f.user = :user2 AND f.friend = :user1)")
    int deleteFriendByUser(@Param("user1") User user1, @Param("user2") User user2);
}
