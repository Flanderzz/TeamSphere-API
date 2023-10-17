package com.chatApp.webSocketAPI.service;

import com.chatApp.webSocketAPI.Exception.UserException;
import com.chatApp.webSocketAPI.model.User;
import com.chatApp.webSocketAPI.request.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    public User findByID(Integer ID) throws UserException;
    public User findUserProfile(String jwt) throws UserException;
    public List<User> searchUsers(String query);
    public User updateUser (Integer ID, UpdateUserRequest request) throws UserException;
}
