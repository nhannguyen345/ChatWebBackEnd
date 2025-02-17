package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    @Query("SELECT p FROM PasswordResetToken p WHERE p.tokenHash = :tokenHash AND p.expirationTime >= CURRENT_TIMESTAMP AND p.used = false")
    Optional<PasswordResetToken> getValidResetTokensForUser(@Param("tokenHash") String tokenHash);

    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken p SET p.used = true WHERE p.tokenHash = :tokenHash")
    int updateUsedToken(@Param("tokenHash") String tokenHash);
}
