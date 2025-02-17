package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.entity.Group;
import com.example.backend.model.entity.Message;
import com.example.backend.model.entity.User;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.sender = :user OR m.receiver = :user) AND m.group IS NULL")
    List<Message> findFriendMessages(@Param("user") User user);

    @Query("SELECT m from Message m WHERE m.group IN (SELECT gm.group FROM GroupMember gm WHERE gm.user = :user)")
    List<Message> findGroupMessages(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE from Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1)")
    int deleteMessagesOfTwoUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Modifying
    @Transactional
    @Query("DELETE from Message m WHERE m.group = :group")
    int deleteMessagesOfGroup(@Param("group") Group group);
}
