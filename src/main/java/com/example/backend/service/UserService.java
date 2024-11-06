package com.example.backend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.PasswordResetToken;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.PasswordUpdateRequest;
import com.example.backend.model.request.PersonalInfoUpdateRequest;
import com.example.backend.model.request.SocialInfoUpdateRequest;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.email.EmailService;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByEmail(username); // Assuming 'email' is used as username

        // Converting UserInfo to UserDetails
        return user.map(UserDetailsCustom::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User addUser(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        } else if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        // Encode password before saving the user
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return repository.save(user);
    }

    public User getUserByEmail(String email) {
        Optional<User> user = repository.findByEmail(email);
        return user.orElseThrow(() -> new RuntimeException("User not found with this email!"));
    }

    public User getUserById(int userId) {
        Optional<User> user = repository.findById(userId);
        return user.orElseThrow(() -> new RuntimeException("User not found with this id!"));
    }

    public int updateUserPersonalInfo(int userId, PersonalInfoUpdateRequest personalInfoUpdateRequest) {
        if (repository.findById(userId).isPresent()) {
            return repository.updateUserPersonalInfo(
                    PersonalInfoUpdateRequest.convertBitrhdate(personalInfoUpdateRequest.getBirthdate()),
                    personalInfoUpdateRequest.getPhone(), personalInfoUpdateRequest.getAddress(),
                    userId);
        }
        throw new IllegalArgumentException("User not found!");
    }

    public int updateUserSocialInfo(int userId, SocialInfoUpdateRequest socialInfoUpdateRequest) {
        if (repository.findById(userId).isPresent()) {
            return repository.updateUserSocialInfo(socialInfoUpdateRequest.getFbLink(),
                    socialInfoUpdateRequest.getInstaLink(), socialInfoUpdateRequest.getTwitterLink(),
                    userId);
        }

        throw new IllegalArgumentException("User not found!");

    }

    public int updateUserPassword(int userId, PasswordUpdateRequest passwordUpdateRequest) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = repository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (passwordEncoder.matches(passwordUpdateRequest.getCurrentPassword(), user.getPassword())) {
            return repository.updateUserPassword(passwordEncoder.encode(passwordUpdateRequest.getCurrentPassword()),
                    userId);
        }
        throw new IllegalArgumentException("Error: the current password doesn't match!");

    }

    public int updateUserAvatar(int userId, String avatarUrl) {
        if (repository.findById(userId).isPresent()) {
            return repository.updateUserAvatar(userId, avatarUrl);
        }

        throw new IllegalArgumentException("User not found!");

    }

    public Boolean checkEmailAndSendResetLinkToUser(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email is invalid!"));

        PasswordResetToken passwordResetToken = passwordResetTokenService.createNewPassReset(user);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("userName", user.getUsername());
        templateModel.put("resetLink",
                "http://localhost:5173/change-password?token=" + passwordResetToken.getTokenHash());

        emailService.sendEmailWithTemplate(email, "Reset password", "reset-password-template", templateModel);

        return true;

    }

    public PasswordResetToken checkToken(String token) {
        return passwordResetTokenService.checkToken(token);
    }

    public int updatePasswordAndStatusToken(int userId, String token, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        int updatedCountPass = repository.updateUserPassword(passwordEncoder.encode(password), userId);
        int updatedCountToken = passwordResetTokenService.updateStatusToken(token);
        ;
        return updatedCountPass + updatedCountToken;
    }

}
