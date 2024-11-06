package co.teamsphere.teamsphere.DTOmapper;

import co.teamsphere.teamsphere.DTO.UserDTO;
import co.teamsphere.teamsphere.models.User;
import org.mapstruct.Mapper;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    UserDTO toUserDTO(User user);
    HashSet<UserDTO> toUserDtos(Set<User> set);
}
