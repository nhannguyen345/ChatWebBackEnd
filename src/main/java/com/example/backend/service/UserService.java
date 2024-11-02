package com.example.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.User;
import com.example.backend.model.request.PasswordUpdateRequest;
import com.example.backend.model.request.PersonalInfoUpdateRequest;
import com.example.backend.model.request.SocialInfoUpdateRequest;
import com.example.backend.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByEmail(username); // Assuming 'email' is used as username

        // Converting UserInfo to UserDetails
        return user.map(UserDetailsCustom::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public String addUser(User user) {
        // Encode password before saving the user
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        repository.save(user);
        return "User Added Successfully";
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

}
