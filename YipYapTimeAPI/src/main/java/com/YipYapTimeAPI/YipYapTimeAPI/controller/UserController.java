package com.YipYapTimeAPI.YipYapTimeAPI.controller;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.UserDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper.UserDTOMapper;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.request.UpdateUserRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUserHandler(@RequestBody UpdateUserRequest req, @PathVariable Integer userId) throws UserException {
        try {
            log.info("Processing update user request for user with ID: {}", userId);

            User updatedUser = userService.updateUser(userId, req);
            UserDTO userDTO = UserDTOMapper.toUserDTO(updatedUser);

            log.info("User with ID {} updated successfully", userId);

            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during update user process for user with ID: {}", userId, e);
            throw new UserException("Error during update user process" + e);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfileHandler(@RequestHeader("Authorization") String jwt) {
        try {
            log.info("Processing get user profile request for user with JWT: {}", jwt);

            User user = userService.findUserProfile(jwt);

            UserDTO userDTO = UserDTOMapper.toUserDTO(user);

            log.info("User profile retrieved successfully for user with JWT: {}", jwt);

            return new ResponseEntity<>(userDTO, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Error during get user profile process", e);
            // We might want to handle this exception differently.
            // Here, I'm letting it propagate to the client as a 500 Internal Server Error.
            throw new RuntimeException("Error during get user profile process", e);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<HashSet<UserDTO>> searchUsersByName(@RequestParam("name") String name) {
        try {
            log.info("Processing search users by name request for name: {}", name);

            List<User> users = userService.searchUser(name);

            HashSet<User> set = new HashSet<>(users);

            HashSet<UserDTO> userDtos = UserDTOMapper.toUserDtos(set);

            log.info("Users search completed successfully for name: {}", name);

            return new ResponseEntity<>(userDtos, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Error during search users process for name: {}", name, e);
            // We might want to handle this exception differently.
            // Here, I'm letting it propagate to the client as a 500 Internal Server Error.
            throw new RuntimeException("Error during search users process", e);
        }
    }
}
