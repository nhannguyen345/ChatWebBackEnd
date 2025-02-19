package com.example.backend.service;

import java.util.List;
import java.util.Map;

import com.example.backend.model.entity.Group;
import com.example.backend.model.entity.Message;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.NewMessageRequest;
import com.example.backend.model.response.Conversation;

public interface MessageService {

    public Map<User, List<Message>> getFriendMessagesForUser(int userId);

    public Map<Group, List<Message>> getGroupMessagesForUser(int userId);

    public List<Conversation> getAllMessagesForUserAndSorted(int userId);

    public Message addNewMessage(NewMessageRequest newMessageRequest);
}
