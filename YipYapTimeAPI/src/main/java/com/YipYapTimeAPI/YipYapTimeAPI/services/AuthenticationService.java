package com.YipYapTimeAPI.YipYapTimeAPI.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.ProfileImageException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.response.AuthResponse;
@Service
public interface AuthenticationService {
    AuthResponse signupUser(String email, String password, String username, MultipartFile imageFile) throws UserException, ProfileImageException;

    AuthResponse loginUser(String username, String password) throws UserException;
}
