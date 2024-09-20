package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.entity.CallHistory;

public interface CallHistoryRepository extends JpaRepository<CallHistory, Integer> {

}
