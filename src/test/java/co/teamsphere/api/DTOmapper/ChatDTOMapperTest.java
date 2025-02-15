package co.teamsphere.api.DTOmapper;

import co.teamsphere.api.DTO.ChatDTO;
import co.teamsphere.api.helpers.TestDataBuilder;
import co.teamsphere.api.models.Chat;
import co.teamsphere.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ChatDTOMapperImpl.class})
public class ChatDTOMapperTest {

    @Autowired
    private ChatDTOMapper mapper;

    @Test
    public void testToChatDto() {
        User testUser = new User();
        testUser.setProfilePicture("testProfilePicture");
        testUser.setPassword("testPassword");
        testUser.setUsername("testUser");
        testUser.setId(UUID.randomUUID());
        Set<User> users = new HashSet<>();
        users.add(testUser);
        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.setChatName("testChat");
        chat.setChatImage("testImage");
        chat.setIsGroup(false);
        chat.setUsers(users);

        ChatDTO chatDto = mapper.toChatDto(chat);

        assertThat(chatDto).isNotNull();
        assertThat(chatDto.getId()).isEqualTo(chat.getId());
        assertThat(chatDto.getChatName()).isEqualTo(chat.getChatName());
        assertThat(chatDto.getChatImage()).isEqualTo(chat.getChatImage());
        assertThat(chatDto.getIsGroup()).isEqualTo(chat.getIsGroup());
        assertThat(chatDto.getUsers()).hasSize(1);

        // Password is not included in the DTO, so we don't assert on it
        UUID userId = chatDto.getUsers().iterator().next();
        assertThat(userId).isNotNull();
        assertThat(userId).isEqualTo(testUser.getId());
    }

    @Test
    public void testToChatDtos() {
        User testUser1 = TestDataBuilder.buildUser(
                "testUser",
                "testProfilePicture",
                "testPassword",
                null);

        User testUser2 = TestDataBuilder.buildUser(
                "testUser2",
                "testProfilePicture2",
                "testPassword2",
                "test2@test2.com");
        Set<User> users1 = new HashSet<>();
        users1.add(testUser1);

        Set<User> users2 = new HashSet<>();
        users2.add(testUser2);

        Chat chat1 = new Chat();
        chat1.setId(UUID.randomUUID());
        chat1.setChatName("testChat2");
        chat1.setChatImage("testImage2");
        chat1.setIsGroup(false);
        chat1.setUsers(users1);

        Chat chat2 = new Chat();

        chat2.setId(UUID.randomUUID());
        chat2.setChatName("testChat2");
        chat2.setChatImage("testImage2");
        chat2.setIsGroup(false);
        chat2.setUsers(users2);

        List<Chat> chats = Arrays.asList(chat1, chat2);
        List<ChatDTO> chatDtos = mapper.toChatDtos(chats);

        assertThat(chatDtos).hasSize(2);
        // Add more assertions to verify the fields
        // Verify first ChatDTO
        ChatDTO chatDto1 = chatDtos.get(0);
        assertThat(chatDto1).isNotNull();
        assertThat(chatDto1.getId()).isEqualTo(chat1.getId());
        assertThat(chatDto1.getChatName()).isEqualTo(chat1.getChatName());
        assertThat(chatDto1.getChatImage()).isEqualTo(chat1.getChatImage());
        assertThat(chatDto1.getIsGroup()).isEqualTo(chat1.getIsGroup());
        assertThat(chatDto1.getUsers()).hasSize(1);

        UUID userId = chatDto1.getUsers().iterator().next();
        assertThat(userId).isNotNull();
        assertThat(userId).isEqualTo(testUser1.getId());

        // Verify second ChatDTO
        ChatDTO chatDto2 = chatDtos.get(1);
        assertThat(chatDto2).isNotNull();
        assertThat(chatDto2.getId()).isEqualTo(chat2.getId());
        assertThat(chatDto2.getChatName()).isEqualTo(chat2.getChatName());
        assertThat(chatDto2.getChatImage()).isEqualTo(chat2.getChatImage());
        assertThat(chatDto2.getIsGroup()).isEqualTo(chat2.getIsGroup());
        assertThat(chatDto2.getUsers()).hasSize(1);

        UUID userId2 = chatDto2.getUsers().iterator().next();
        assertThat(userId2).isNotNull();
        assertThat(userId2).isEqualTo(testUser2.getId());
    }
}
