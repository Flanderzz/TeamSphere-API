package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.response.AuthResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AuthenticationService {
    AuthResponse signupUser(String email, String password, String username, MultipartFile imageFile) throws UserException;

    AuthResponse loginUser(String username, String password) throws UserException;
}
