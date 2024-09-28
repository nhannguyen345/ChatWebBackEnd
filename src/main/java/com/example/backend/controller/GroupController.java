package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.entity.GroupMember;
import com.example.backend.model.request.GroupCreationRequest;
import com.example.backend.service.GroupService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/create-new-group")
    public ResponseEntity<?> createNewGroup(@RequestBody GroupCreationRequest groupCreationRequest) {
        try {
            if (groupCreationRequest.getListUsers().size() < 3) {
                throw new IllegalArgumentException("A group must have more than two people!");
            }
            List<GroupMember> groupMembers = groupService.addNewGroup(groupCreationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(groupMembers);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error has occurred!");
        }
    }

}
