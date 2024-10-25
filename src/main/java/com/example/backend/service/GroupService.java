package com.example.backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.entity.Group;
import com.example.backend.model.entity.GroupMember;
import com.example.backend.model.entity.Notification;
import com.example.backend.model.entity.User;
import com.example.backend.model.entity.Notification.NotificationType;
import com.example.backend.model.request.GroupCreationRequest;
import com.example.backend.repository.GroupMemberRepository;
import com.example.backend.repository.GroupRepository;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GroupService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    public List<GroupMember> getListGroupsForUser(User user) {
        return groupMemberRepository.findAllGroupsForUser(user);
    }

    public List<GroupMember> getListGroupMembersForUser(long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return groupMemberRepository.findByGroup(group);
    }

    @Transactional
    public List<GroupMember> addNewGroup(GroupCreationRequest groupCreationRequest, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Add new group
        Group newGroup = new Group();
        newGroup.setName(groupCreationRequest.getGroupName());
        newGroup.setAvatarUrl(groupCreationRequest.getUrlImage());
        newGroup.setCreatedBy(user);

        groupRepository.save(newGroup);

        List<GroupMember> lGroupMembers = new ArrayList<>();

        List<Integer> listMembers = groupCreationRequest.getListUsers();
        listMembers.add(userId);

        // Add group members
        for (int memberId : listMembers) {
            User joinedUser = userRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            GroupMember groupMember = new GroupMember();
            groupMember.setGroup(newGroup);
            groupMember.setUser(joinedUser);
            lGroupMembers.add(groupMember);
        }

        groupMemberRepository.saveAll(lGroupMembers);

        CompletableFuture<Void> future = sendGroupNotificationAsync(lGroupMembers, user, groupCreationRequest);
        future.exceptionally(e -> {
            log.info("Error at addNewGroup - GroupService: {}", e.getMessage());
            return null;
        });

        return lGroupMembers;
    }

    @Async
    public CompletableFuture<Void> sendGroupNotificationAsync(List<GroupMember> groupMembers, User groupCreator,
            GroupCreationRequest groupCreationRequest) {
        try {
            for (GroupMember groupMember : groupMembers) {
                if (!groupCreator.equals(groupMember.getUser())) {
                    Notification notification = new Notification();
                    notification.setSender(groupCreator);
                    notification.setReceiver(groupMember.getUser());
                    notification.setContent(" has add you to new group (" + groupCreationRequest.getGroupName() + ")");
                    notification.setNotificationType(NotificationType.ADD_NEW_GROUP);

                    Map<String, Object> notificationAndGroupInfo = new HashMap<>();
                    notificationAndGroupInfo.put("notification", notificationRepository.save(notification));
                    notificationAndGroupInfo.put("groupMember", groupMember);

                    simpMessagingTemplate.convertAndSendToUser(groupMember.getUser().getUsername(),
                            "/queue/notification",
                            notificationAndGroupInfo);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }
}
