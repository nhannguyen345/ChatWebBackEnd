package com.example.backend.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketEventListener {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static List<String> usersOnline = new ArrayList<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String username = accessor.getUser().getName();
        System.out.println(username + " - " + sessionId);

        if (!usersOnline.contains(username)) {
            usersOnline.add(username);
            // messagingTemplate.convertAndSend("/topic/getUsersOnline", usersOnline);
        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = accessor.getUser().getName();
        if (!usersOnline.stream().filter(name -> name == username).collect(Collectors.toList()).isEmpty()) {
            usersOnline.remove(username);
            messagingTemplate.convertAndSend("/topic/getUsersOnline", usersOnline);
        }
    }

    public List<String> getListUsersOnline() {
        return usersOnline;
    }
}
