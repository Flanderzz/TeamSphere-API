package co.teamsphere.api.DTOmapper;

import co.teamsphere.api.DTO.UserDTO;
import co.teamsphere.api.models.User;
import org.mapstruct.Mapper;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    UserDTO toUserDTO(User user);
    HashSet<UserDTO> toUserDtos(Set<User> set);
}
