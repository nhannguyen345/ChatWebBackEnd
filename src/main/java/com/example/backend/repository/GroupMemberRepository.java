package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.model.entity.Group;
import com.example.backend.model.entity.GroupMember;
import com.example.backend.model.entity.User;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("SELECT gm FROM GroupMember gm WHERE gm.user = :user")
    List<GroupMember> findAllGroupsForUser(@Param("user") User user);

    List<GroupMember> findByGroup(Group group);
}
