package com.example.backend.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.PasswordResetToken;
import com.example.backend.model.entity.User;
import com.example.backend.repository.PasswordResetTokenRepository;
import com.example.backend.service.token.TokenGenerator;

@Service
public class PasswordResetTokenService {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public Date calculateExpirationDate(Date createdDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdDate);
        calendar.add(Calendar.MINUTE, 10);
        return calendar.getTime();
    }

    public PasswordResetToken createNewPassReset(User user) {
        Date createdDate = new Date();
        Date expDate = calculateExpirationDate(createdDate);

        String newToken = TokenGenerator.generateToken();

        PasswordResetToken pToken = new PasswordResetToken();
        pToken.setCreatedAt(createdDate);
        pToken.setExpirationTime(expDate);
        pToken.setTokenHash(newToken);
        pToken.setUser(user);

        return passwordResetTokenRepository.save(pToken);
    }

    public PasswordResetToken checkToken(String token) {
        return passwordResetTokenRepository.getValidResetTokensForUser(token)
                .orElseThrow(() -> new RuntimeException("Token is invalid!"));
    }

    public int updateStatusToken(String token) {
        return passwordResetTokenRepository.updateUsedToken(token);
    }
}
