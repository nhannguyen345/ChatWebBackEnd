package com.example.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.User;
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
}
