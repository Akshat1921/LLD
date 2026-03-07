package com.example.LLD.Splitwise.user;

import java.util.ArrayList;
import java.util.List;

import com.example.LLD.Splitwise.exceptions.UserAlreadyExistsException;
import com.example.LLD.Splitwise.exceptions.UserNotFoundException;


public class UserController {
    private List<User> userList;

    public UserController(){
        userList = new ArrayList<>();
    }

    public void addUser(User user){
        if(!userList.contains(user)){
            userList.add(user);
        }else{
            throw new UserAlreadyExistsException("User already exists");
        }
    }

    public User getUser(String userId){
        return userList.stream()
            .filter(e->e.getUserId().equals(userId))
            .findAny()
            .orElseThrow(()-> new UserNotFoundException("User not found: " + userId));
    }

    public List<User> getAllUsers(){
        return userList;
    }

}
