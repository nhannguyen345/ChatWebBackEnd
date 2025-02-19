package com.example.backend.service.implementation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.Friend;
import com.example.backend.model.entity.Group;
import com.example.backend.model.entity.GroupMember;
import com.example.backend.model.entity.Message;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.NewMessageRequest;
import com.example.backend.model.response.Conversation;
import com.example.backend.repository.FriendRepository;
import com.example.backend.repository.GroupMemberRepository;
import com.example.backend.repository.GroupRepository;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Map<User, List<Message>> getFriendMessagesForUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> friendMessages = messageRepository.findFriendMessages(user);

        List<Friend> friends = friendRepository.findAllFriendsForUser(user);

        Map<User, List<Message>> messagesByFriend = new HashMap<>();

        for (Friend friendRelation : friends) {
            User friend = friendRelation.getUser().equals(user) ? friendRelation.getFriend() : friendRelation.getUser();
            messagesByFriend.putIfAbsent(friend, new ArrayList<>());
        }

        for (Message message : friendMessages) {
            User friend = message.getSender().equals(user) ? message.getReceiver() : message.getSender();
            messagesByFriend.computeIfAbsent(friend, k -> new ArrayList<>())
                    .add(message);
        }

        return messagesByFriend;
    }

    @Override
    public Map<Group, List<Message>> getGroupMessagesForUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> groupMessages = messageRepository.findGroupMessages(user);

        List<GroupMember> groupMembers = groupMemberRepository.findAllGroupsForUser(user);

        Map<Group, List<Message>> messagesByGroup = new HashMap<>();

        for (GroupMember groupMember : groupMembers) {
            Group group = groupMember.getGroup();
            messagesByGroup.putIfAbsent(group, new ArrayList<>());
        }

        for (Message message : groupMessages) {
            Group group = message.getGroup();
            messagesByGroup.computeIfAbsent(group, k -> new ArrayList<>())
                    .add(message);
        }

        return messagesByGroup;
    }

    @Override
    public List<Conversation> getAllMessagesForUserAndSorted(int userId) {
        List<Conversation> allConversations = new ArrayList<>();

        Map<User, List<Message>> friendMessages = getFriendMessagesForUser(userId);
        for (Map.Entry<User, List<Message>> entry : friendMessages.entrySet()) {
            List<Message> messages = entry.getValue();
            Date lastMessageTime = messages.isEmpty() ? null : messages.get(messages.size() - 1).getCreatedAt();
            allConversations.add(new Conversation("friend", entry.getKey(), lastMessageTime, messages));
        }

        Map<Group, List<Message>> groupMessages = getGroupMessagesForUser(userId);
        for (Map.Entry<Group, List<Message>> entry : groupMessages.entrySet()) {
            List<Message> messages = entry.getValue();
            Date lastMessageTime = messages.isEmpty() ? null : messages.get(messages.size() - 1).getCreatedAt();
            allConversations.add(new Conversation("group", entry.getKey(), lastMessageTime, messages));
        }

        allConversations.sort((c1, c2) -> {
            if (c1.getLastMessageTime() == null && c2.getLastMessageTime() == null)
                return 0;
            if (c1.getLastMessageTime() == null)
                return 1;
            if (c2.getLastMessageTime() == null)
                return -1;
            return c2.getLastMessageTime().compareTo(c1.getLastMessageTime());
        });

        return allConversations;
    }

    @Override
    public Message addNewMessage(NewMessageRequest newMessageRequest) {

        Message message = new Message();

        User sender = userRepository.findById(newMessageRequest.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        message.setSender(sender);

        if (newMessageRequest.getReceiverId() != null) {
            User receiver = userRepository.findById(newMessageRequest.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            message.setReceiver(receiver);
        } else if (newMessageRequest.getGroupId() != null) {
            Group group = groupRepository.findById(newMessageRequest.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            message.setGroup(group);
        }

        message.setContent(newMessageRequest.getContent());
        message.setFileUrl(newMessageRequest.getFileUrl());
        message.setMessageType(Message.MessageType.valueOf(newMessageRequest.getMessageType()));

        return messageRepository.save(message);
    }
}
