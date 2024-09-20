package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {

}
