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

    public int updateUserPersonalInfo(PersonalInfoUpdateRequest personalInfoUpdateRequest) {
        return repository.updateUserPersonalInfo(
                PersonalInfoUpdateRequest.convertBitrhdate(personalInfoUpdateRequest.getBirthdate()),
                personalInfoUpdateRequest.getPhone(), personalInfoUpdateRequest.getAddress(),
                personalInfoUpdateRequest.getUserId());
    }

    public int updateUserSocialInfo(SocialInfoUpdateRequest socialInfoUpdateRequest) {
        return repository.updateUserSocialInfo(socialInfoUpdateRequest.getFbLink(),
                socialInfoUpdateRequest.getInstaLink(), socialInfoUpdateRequest.getTwitterLink(),
                socialInfoUpdateRequest.getUserId());
    }

    public int updateUserPassword(PasswordUpdateRequest passwordUpdateRequest) {
        return repository.updateUserPassword(new BCryptPasswordEncoder().encode(passwordUpdateRequest.getPassword()),
                passwordUpdateRequest.getUserId());
    }

}
