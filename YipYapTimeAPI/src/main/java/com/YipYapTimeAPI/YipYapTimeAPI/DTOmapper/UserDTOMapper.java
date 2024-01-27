package com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.UserDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;

import java.util.HashSet;
import java.util.Set;

public class UserDTOMapper {

    public static UserDTO toUserDTO(User user) {

        UserDTO userDto=new UserDTO();
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setId(user.getId());
        userDto.setProfile_picture(user.getProfile_image());

        return userDto;

    }

    public static HashSet<UserDTO> toUserDtos(Set<User> set){
        HashSet<UserDTO> userDtos=new HashSet<>();

        for(User user:set) {
            UserDTO userDto=toUserDTO(user);
            userDtos.add(userDto);
        }

        return userDtos;
    }
}
