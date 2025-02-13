package co.teamsphere.api.DTOmapper;

import co.teamsphere.api.DTO.UserDTO;
import co.teamsphere.api.models.User;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-11T20:03:06+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserDTOMapperImpl implements UserDTOMapper {

    @Override
    public UserDTO toUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( user.getId() );
        userDTO.username( user.getUsername() );
        userDTO.email( user.getEmail() );
        userDTO.profilePicture( user.getProfilePicture() );

        return userDTO.build();
    }

    @Override
    public HashSet<UserDTO> toUserDtos(Set<User> set) {
        if ( set == null ) {
            return null;
        }

        HashSet<UserDTO> hashSet = new HashSet<UserDTO>();
        for ( User user : set ) {
            hashSet.add( toUserDTO( user ) );
        }

        return hashSet;
    }
}
