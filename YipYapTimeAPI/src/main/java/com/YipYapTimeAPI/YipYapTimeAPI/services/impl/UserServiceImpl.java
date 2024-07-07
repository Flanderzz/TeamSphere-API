package com.YipYapTimeAPI.YipYapTimeAPI.services.impl;

import com.YipYapTimeAPI.YipYapTimeAPI.config.JWTTokenProvider;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.UserRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.request.UpdateUserRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    private final JWTTokenProvider jwtTokenProvider;

    public UserServiceImpl( UserRepository userRepo, JWTTokenProvider jwtTokenProvider) {
        this.userRepo = userRepo;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User updateUser(UUID userId, UpdateUserRequest req) throws UserException {

        log.info("Attempting to update user with ID: {}", userId);
        User user = findUserById(userId);

        if (user == null) {
            log.error("User with ID= {}, not found. Unable to update.", userId);
            throw new UserException("User not found");
        }

        log.info("Found user for update: {}", user);

        if (req.getUsername() != null) {
            log.info("Updating username to: {}", req.getUsername());
            user.setUsername(req.getUsername());
        }
        if (req.getProfile_picture() != null) {
            log.info("Updating profile picture to: {}", req.getProfile_picture());
            user.setProfilePicture(req.getProfile_picture());
        }

        // Save the updated user
        User updatedUser = userRepo.save(user);
        log.info("User updated successfully. Updated user details: {}", updatedUser);

        return updatedUser;
    }

    @Override
    public User findUserById(UUID userId) throws UserException {
        log.info("Attempting to find user by ID: {}", userId);

        Optional<User> opt = userRepo.findById(userId);

        if (opt.isPresent()) {
            User user = opt.get();
            log.info("Found user with ID {}: {}", userId, user);

            return user;
        }
        throw new UserException("user doesnt exist with the id: " + userId);
    }

    @Override
    public User findUserProfile(String jwt) {
        log.info("Attempting to find user profile using JWT");

        String email = jwtTokenProvider.getEmailFromToken(jwt);

        Optional<User> opt = userRepo.findByEmail(email);

        if (opt.isPresent()) {
            log.info("Found user profile for userId: {}", opt.get().getId());
            return opt.get();
        }

        log.error("User profile not found for email: {}", email);

        throw new BadCredentialsException("Received invalid token!");
    }

    @Override
    public List<User> searchUser(String query) {
        log.info("Searching users with query: {}", query);

        List<User> searchResults = userRepo.searchUsers(query);

        if (!searchResults.isEmpty()) {
            log.info("Found {} user(s) matching the query.", searchResults.size());
            return searchResults;
        }

        log.info("No users found matching the query: {}", query);
        return Collections.emptyList();
    }
}