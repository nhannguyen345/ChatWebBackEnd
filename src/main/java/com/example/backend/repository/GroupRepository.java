package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Integer> {

}
