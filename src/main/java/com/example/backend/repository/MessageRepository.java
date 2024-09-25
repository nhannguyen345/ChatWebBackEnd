package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
