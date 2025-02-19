package com.example.backend.service;

import java.util.Date;

import com.example.backend.model.entity.PasswordResetToken;
import com.example.backend.model.entity.User;

public interface PasswordResetTokenService {

    public Date calculateExpirationDate(Date createdDate);

    public PasswordResetToken createNewPassReset(User user);

    public PasswordResetToken checkToken(String token);

    public int updateStatusToken(String token);
}
