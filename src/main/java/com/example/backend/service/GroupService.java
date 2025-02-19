package com.example.backend.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.entity.GroupMember;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.GroupCreationRequest;

public interface GroupService {

    public List<GroupMember> getListGroupsForUser(User user);

    public List<GroupMember> getListGroupMembersForUser(long groupId);

    @Transactional
    public List<GroupMember> addNewGroup(GroupCreationRequest groupCreationRequest, int userId);

    public CompletableFuture<Void> sendGroupNotificationAsync(List<GroupMember> groupMembers, User groupCreator,
            GroupCreationRequest groupCreationRequest);

    public void deleteMemberFromGroup(int userId, Long groupId);

}
