package com.YipYapTimeAPI.YipYapTimeAPI.controller;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.UserDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper.UserDTOMapper;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.request.UpdateUserRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
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
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUserHandler(@RequestBody UpdateUserRequest req, @PathVariable Integer userId) throws UserException {
        User updatedUser = userService.updateUser(userId, req);
        UserDTO UserDTO = UserDTOMapper.toUserDTO(updatedUser);

        return new ResponseEntity<UserDTO>(UserDTO, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfileHandler(@RequestHeader("Authorization")String jwt){

        User user = userService.findUserProfile(jwt);

        UserDTO UserDTO = UserDTOMapper.toUserDTO(user);

        return new ResponseEntity<UserDTO>(UserDTO,HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    public ResponseEntity<HashSet<UserDTO>> searchUsersByName(@RequestParam("name") String name) {

        List<User> users=userService.searchUser(name);

        HashSet<User> set=new HashSet<>(users);

        HashSet<UserDTO> userDtos=UserDTOMapper.toUserDtos(set);

        return new ResponseEntity<>(userDtos,HttpStatus.ACCEPTED);
    }
}
