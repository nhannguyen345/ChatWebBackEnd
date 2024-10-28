package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.entity.Group;
import com.example.backend.model.entity.GroupMember;
import com.example.backend.model.entity.User;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("SELECT gm FROM GroupMember gm WHERE gm.user = :user")
    List<GroupMember> findAllGroupsForUser(@Param("user") User user);

    List<GroupMember> findByGroup(Group group);

    Optional<GroupMember> findByGroupAndUser(Group group, User user);

    @Modifying
    @Transactional
    @Query("DELETE from GroupMember g WHERE g.group = :group AND g.user = :member")
    int deleteMemberFromGroup(@Param("member") User member, @Param("group") Group group);

}
