package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.entity.Call;
import com.example.backend.model.entity.User;

public interface CallRepository extends JpaRepository<Call, Long> {
    @Modifying
    @Transactional
    @Query("SELECT c FROM Call c WHERE c.caller = :user OR c.receiver = :user ORDER BY c.endedAt DESC")
    public List<Call> findUserCall(@Param("user") User user);
}
