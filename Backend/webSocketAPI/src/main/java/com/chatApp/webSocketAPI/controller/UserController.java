package com.chatApp.webSocketAPI.controller;

import com.chatApp.webSocketAPI.Exception.UserException;
import com.chatApp.webSocketAPI.model.User;
import com.chatApp.webSocketAPI.request.UpdateUserRequest;
import com.chatApp.webSocketAPI.response.ApiResponse;
import com.chatApp.webSocketAPI.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/users")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfileHandler(@RequestHeader("Authorization") String token) throws UserException {

        User user = userService.findUserProfile(token);

        return new ResponseEntity<User>(user, ACCEPTED);
    }

    @GetMapping("/{query}")
    public ResponseEntity<List<User>> searchUserHandler(@PathVariable("query") String qry){
        List<User> userSearch = userService.searchUsers(qry);

        return new ResponseEntity<List<User>>(userSearch, OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUserHandler(@RequestBody UpdateUserRequest request, @RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfile(token);
        userService.updateUser(user.getID(), request);

        ApiResponse apiResponse = new ApiResponse("User Updated", true);

        return new ResponseEntity<ApiResponse>(apiResponse, ACCEPTED);
    }
}
