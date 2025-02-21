package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.GroupMember;
import com.example.backend.model.request.GroupCreationRequest;
import com.example.backend.model.request.GroupMemberRequest;
import com.example.backend.service.GroupService;
import com.example.backend.service.UserDetailsCustom;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/get-members-group")
    public List<GroupMember> getListMembersOfGroup(@RequestBody GroupMemberRequest groupMemberRequest) {
        return groupService.getListGroupMembersForUser(groupMemberRequest.getGroupId());
    }

    @PostMapping("/create-new-group")
    public ResponseEntity<?> createNewGroup(@RequestBody GroupCreationRequest groupCreationRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                if (groupCreationRequest.getListUsers().size() < 2) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("A group must have more than two people!");
                }
                List<GroupMember> groupMembers = groupService.addNewGroup(groupCreationRequest, uDetailsCustom.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(groupMembers);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error has occurred!");
        }
    }

    @DeleteMapping("/leave-group/{groupId}")
    public ResponseEntity<?> deleteMemberFromGroup(@PathVariable Long groupId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCustom) {
                UserDetailsCustom uDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
                groupService.deleteMemberFromGroup(uDetailsCustom.getId(), groupId);
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token contains invalid information!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error has occurred!");
        }
    }

}
