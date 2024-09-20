package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.entity.Call;

public interface CallRepository extends JpaRepository<Call, Integer> {

}
