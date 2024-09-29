package com.example.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.entity.Group;
import com.example.backend.model.entity.GroupMember;
import com.example.backend.model.entity.User;
import com.example.backend.model.request.GroupCreationRequest;
import com.example.backend.repository.GroupMemberRepository;
import com.example.backend.repository.GroupRepository;
import com.example.backend.repository.UserRepository;

@Service
public class GroupService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    public List<GroupMember> addNewGroup(GroupCreationRequest groupCreationRequest) {
        User user = userRepository.findById(groupCreationRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Add new group
        Group newGroup = new Group();
        newGroup.setName(groupCreationRequest.getGroupName());
        newGroup.setAvatarUrl(groupCreationRequest.getUrlImage());
        newGroup.setCreatedBy(user);

        groupRepository.save(newGroup);

        List<GroupMember> lGroupMembers = new ArrayList<>();

        // Add group members
        for (int userId : groupCreationRequest.getListUsers()) {
            User joinedUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            GroupMember groupMember = new GroupMember();
            groupMember.setGroup(newGroup);
            groupMember.setUser(joinedUser);

            groupMemberRepository.save(groupMember);
            lGroupMembers.add(groupMember);
        }

        return lGroupMembers;
    }
}
