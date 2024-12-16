package com.YipYapTimeAPI.YipYapTimeAPI.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.ProfileImageException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.request.SignupRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.response.AuthResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Service
public interface AuthenticationService {
    AuthResponse signupUser(@Valid SignupRequest request) throws UserException, ProfileImageException;

    AuthResponse loginUser(String username, String password) throws UserException;
}
