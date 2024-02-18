package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.request.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService{

    User findUserProfile(String jwt);

    User updateUser(Integer userId, UpdateUserRequest req) throws UserException;

    User findUserById(Integer userId) throws UserException;

    List<User> searchUser(String query);
}
