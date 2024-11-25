package com.YipYapTimeAPI.YipYapTimeAPI.controller;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.UserDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper.UserDTOMapper;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.request.UpdateUserRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    private final UserService userService;

    private final UserDTOMapper userDTOMapper;
    public UserController(UserService userService,
                          UserDTOMapper  userDTOMapper) {
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
    }

    @Operation(summary = "Update user details", description = "Updates the details of a user identified by the given user ID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UserDTO.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUserHandler(@RequestBody UpdateUserRequest req, @PathVariable UUID userId) throws UserException {
        try {
            log.info("Processing update user request for user with ID: {}", userId);

            User updatedUser = userService.updateUser(userId, req);
            UserDTO userDTO = userDTOMapper.toUserDTO(updatedUser);

            log.info("User with ID {} updated successfully", userId);

            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during update user process for user with ID: {}", userId, e);
            throw new UserException("Error during update user process" + e);
        }
    }

    @Operation(summary = "Get user profile", description = "Fetches the profile of the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "User profile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UserDTO.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfileHandler(@RequestHeader("Authorization") String jwt) {
        try {
            log.info("Processing get user profile request");

            User user = userService.findUserProfile(jwt);

            UserDTO userDTO = userDTOMapper.toUserDTO(user);

            log.info("User profile retrieved successfully");

            return new ResponseEntity<>(userDTO, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Error during get user profile process", e);
            // We might want to handle this exception differently.
            // Here, I'm letting it propagate to the client as a 500 Internal Server Error.
            throw new RuntimeException("Error during get user profile process", e);
        }
    }

    @Operation(summary = "Search users by name", description = "Searches for users whose names match the provided query.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Search completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = HashSet.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<HashSet<UserDTO>> searchUsersByName(@RequestParam("name") String name) {
        try {
            log.info("Processing search users by name={}", name);

            List<User> users = userService.searchUser(name);

            HashSet<User> set = new HashSet<>(users);

            HashSet<UserDTO> userDtos = userDTOMapper.toUserDtos(set);

            log.info("Users search completed successfully for name={}", name);

            return new ResponseEntity<>(userDtos, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Error during search users process", e);
            // We might want to handle this exception differently.
            // Here, I'm letting it propagate to the client as a 500 Internal Server Error.
            throw new RuntimeException("Error during search users process", e);
        }
    }
}
