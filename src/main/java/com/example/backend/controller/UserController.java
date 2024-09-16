package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.User;
import com.example.backend.model.request.AuthRequest;
import com.example.backend.model.response.AuthResponse;
import com.example.backend.service.JwtService;
import com.example.backend.service.UserDetailsCustom;
import com.example.backend.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String addnewuser(@RequestBody User user) {
        return service.addUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateAndgetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            UserDetails userLogin = service.loadUserByUsername(authRequest.getEmail());

            if (userLogin instanceof UserDetailsCustom) {
                UserDetailsCustom userDetailsCustom = (UserDetailsCustom) userLogin;
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new AuthResponse(userDetailsCustom.getUsername(),
                                jwtService.generateToken(authRequest.getEmail()), true, "Login successfully!"));
            } else
                throw new UsernameNotFoundException("User details not found!");
        } else {
            throw new UsernameNotFoundException("Invalid email request!");
        }
    }

}
