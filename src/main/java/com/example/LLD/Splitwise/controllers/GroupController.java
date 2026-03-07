package com.example.LLD.Splitwise.controllers;

import java.util.ArrayList;
import java.util.List;

import com.example.LLD.Splitwise.group.Group;
import com.example.LLD.Splitwise.user.User;

public class GroupController {
    private List<Group> groupList;
    public GroupController(){
        groupList = new ArrayList<>();
    }

    public Group createGroup(String groupId, String groupName, User createdByUser){
        Group group = new Group();
        group.addMember(createdByUser);
        group.setGroupId(groupId);
        group.setGroupName(groupName);
        groupList.add(group);
        return group;
    }

    public Group getGroup(String groupId){
        for(Group group: groupList) {
            if(group.getGroupId().equals(groupId)){
                return group;
            }
        }
        System.out.println("No such group exist!");
        return null;
    }

    public List<Group> getAllGroups(){
        return groupList;
    }

}
