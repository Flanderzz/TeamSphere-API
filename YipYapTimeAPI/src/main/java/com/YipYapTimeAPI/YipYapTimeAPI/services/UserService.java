package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.request.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService{

    public User findUserProfile(String jwt);

    public User updateUser(Integer userId, UpdateUserRequest req) throws UserException;

    public User findUserById(Integer userId) throws UserException;

    public List<User> searchUser(String query);
}
