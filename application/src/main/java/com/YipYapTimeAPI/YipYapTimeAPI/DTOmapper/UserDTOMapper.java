package com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.UserDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import org.mapstruct.Mapper;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    UserDTO toUserDTO(User user);
    HashSet<UserDTO> toUserDtos(Set<User> set);
}
