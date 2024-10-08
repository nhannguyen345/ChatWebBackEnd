package com.example.backend.config.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.backend.service.JwtService;
import com.example.backend.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("WS-Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                try {
                    String email = jwtService.extractEmail(token);
                    log.info("Extracted email: {}", email);
                    UserDetails userDetails = userService.loadUserByUsername(email);
                    if (jwtService.validateToken(token, userDetails)) {
                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, null);
                        accessor.setUser(auth);
                        // accessor.setHeaderIfAbsent("session-ws-id", accessor.getSessionId());
                        // accessor.setSessionId(accessor.getSessionId());
                        log.info("info websocket sessionId: {}", accessor.getSessionId());
                    } else {
                        throw new IllegalArgumentException("Token không hợp lệ");
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid Authorization Token");
                }
            } else {
                throw new IllegalArgumentException("Authorization header is missing or invalid");
            }
        }

        return message;
    }
}
