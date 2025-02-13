package co.teamsphere.api.DTOmapper;

import co.teamsphere.api.DTO.UserDTO;
import co.teamsphere.api.helpers.TestDataBuilder;
import co.teamsphere.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserDTOMapperImpl.class})
public class UserDTOMapperTest {

    @Autowired
    private UserDTOMapper mapper;

    @Test
    public void testToUserDTO() {
        // Prepare test data
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("test");
        user.setPassword("password"); // Password should not be included in UserDTO
        user.setProfilePicture("profile picture");
        user.setEmail("test@test.com");

        // Map User to UserDTO
        UserDTO userDto = mapper.toUserDTO(user);

        // Assertions to verify the fields
        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDto.getProfilePicture()).isEqualTo(user.getProfilePicture());
        assertThat(userDto.getEmail()).isNotNull();
    }


    @Test
    public void testToUserDtos() {
        // Prepare test data
        User user1 = TestDataBuilder.buildUser("user1", "profilePic1", "password1", "user1@example.com");
        User user2 = TestDataBuilder.buildUser("user2", "profilePic2", "password2", null);

        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        // Map Set<User> to HashSet<UserDTO>
        HashSet<UserDTO> userDtos = mapper.toUserDtos(users);

        // Assertions to verify the fields
        assertThat(userDtos).isNotNull();

        // Assert that userDtos contains UserDTOs with correct data
        UserDTO userDto1 = userDtos.stream()
                .filter(dto -> dto.getId().equals(user1.getId()))
                .findFirst()
                .orElse(null);

        UserDTO userDto2 = userDtos.stream()
                .filter(dto -> dto.getId().equals(user2.getId()))
                .findFirst()
                .orElse(null);

        assertThat(userDto1).isNotNull();
        assertThat(userDto1.getId()).isEqualTo(user1.getId());
        assertThat(userDto1.getUsername()).isEqualTo(user1.getUsername());
        assertThat(userDto1.getProfilePicture()).isEqualTo(user1.getProfilePicture());
        assertThat(userDto1.getEmail()).isNotNull();

        assertThat(userDto2).isNotNull();
        assertThat(userDto2.getId()).isEqualTo(user2.getId());
        assertThat(userDto2.getUsername()).isEqualTo(user2.getUsername());
        assertThat(userDto2.getProfilePicture()).isEqualTo(user2.getProfilePicture());
        // Assert that email is not included in the UserDTO
        assertThat(userDto2.getEmail()).isNull();
    }
}
