package com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.UserDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;

import java.util.HashSet;
import java.util.Set;

public class UserDTOMapper {

//    private ModelMapper modelMapper;

    public static UserDTO toUserDTO(User user) {

        UserDTO userDto = UserDTO.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .id(user.getId())
                .phone(user.getPhone())
                .profile_picture(user.getProfile_image())
                .build();

        return userDto;
    }

    public static HashSet<UserDTO> toUserDtos(Set<User> set){
        HashSet<UserDTO> userDtos = new HashSet<>();

        for(User user:set) {
            UserDTO userDto=toUserDTO(user);
            userDtos.add(userDto);
        }
        return userDtos;
    }
}
