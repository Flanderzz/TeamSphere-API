package co.teamsphere.api.repository;

import co.teamsphere.api.models.Chat;
import co.teamsphere.api.models.Messages;
import co.teamsphere.api.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class UserRepositoryTests {

    @MockBean
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messagesRepository;

    @Test
    void findUserByEmailTest() {
        User user = User.builder()
                .email("test@gmail.com")
                .username("tester")
                .password("securePassword")
                .phoneNumber("1234567890")
                .createdDate(OffsetDateTime.now())
                .lastUpdatedDate(OffsetDateTime.now())
                .build();

        userRepository.saveAndFlush(user);

        Optional<User> foundUser = userRepository.findByEmail("test@gmail.com");

        assertTrue(foundUser.isPresent());
        assertEquals("tester", foundUser.get().getUsername());
    }

    @Test
    void findUserByUserName() {
        User user = User.builder()
                .username("Lancer")
                .password("securePass")
                .email("lancer@example.com")
                .phoneNumber("9876543210")
                .createdDate(OffsetDateTime.now())
                .lastUpdatedDate(OffsetDateTime.now())
                .build();

        userRepository.saveAndFlush(user);

        Optional<User> findUser = userRepository.findByUsername("Lancer");

        assertTrue(findUser.isPresent());
        assertEquals("Lancer", findUser.get().getUsername());
    }

    @Test
    void searchUser() {
        User user1 = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("password1")
                .phoneNumber("1234567891")
                .createdDate(OffsetDateTime.now())
                .lastUpdatedDate(OffsetDateTime.now())
                .build();

        User user2 = User.builder()
                .username("jane_doe")
                .email("jane@example.com")
                .password("password2")
                .phoneNumber("1234567892")
                .createdDate(OffsetDateTime.now())
                .lastUpdatedDate(OffsetDateTime.now())
                .build();

        User user3 = User.builder()
                .username("doejohn")
                .email("doejohn@example.com")
                .password("password3")
                .phoneNumber("1234567893")
                .createdDate(OffsetDateTime.now())
                .lastUpdatedDate(OffsetDateTime.now())
                .build();

        userRepository.saveAllAndFlush(List.of(user1, user2, user3));

        List<User> searchedUser = userRepository.searchUsers("doe");

        assertEquals(3, searchedUser.size());
        assertTrue(searchedUser.stream().anyMatch(u -> u.getUsername().equals("john_doe")));
        assertTrue(searchedUser.stream().anyMatch(u -> u.getUsername().equals("jane_doe")));
        assertTrue(searchedUser.stream().anyMatch(u -> u.getUsername().equals("doejohn")));
    }

    @Test
    void findMessageByChatIdTest() {

        Chat chat = new Chat();
        chat.setChatName("Test Chat");
        chatRepository.saveAndFlush(chat);


        Messages message1 = new Messages();
        message1.setChat(chat);
        message1.setTimeStamp(LocalDateTime.now().minusMinutes(10));
        message1.setContent("First message");
        messagesRepository.saveAndFlush(message1);

        Messages message2 = new Messages();
        message2.setChat(chat);
        message2.setTimeStamp(LocalDateTime.now().minusMinutes(5));
        message2.setContent("Second message");
        messagesRepository.saveAndFlush(message2);


        List<Messages> messages = messagesRepository.findMessageByChatId(chat.getId());

        assertFalse(messages.isEmpty());
        assertEquals(2, messages.size());
    }


    @Test
    void findChatsByUserIdTest() {
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .email("testuser@example.com")
                .phoneNumber("5556667777")
                .createdDate(OffsetDateTime.now())
                .lastUpdatedDate(OffsetDateTime.now())
                .build();

        userRepository.saveAndFlush(user);

        Chat chat1 = new Chat();
        chat1.setUsers(Set.of(user));
        chatRepository.saveAndFlush(chat1);

        Chat chat2 = new Chat();
        chat2.setUsers(Set.of(user));
        chatRepository.saveAndFlush(chat2);

        Page<Chat> chats = chatRepository.findChatsByUserId(user.getId(), PageRequest.of(0, 10));

        assertFalse(chats.isEmpty());
        assertEquals(2, chats.getTotalElements());
    }

    @Test
    void findSingleChatByUsersIdTest() {
        User user1 = User.builder()
                .username("user1")
                .password("pass1")
                .email("user1@example.com")
                .phoneNumber("1112223333")
                .createdDate(OffsetDateTime.now())
                .lastUpdatedDate(OffsetDateTime.now())
                .build();

        User user2 = User.builder()
                .username("user2")
                .password("pass2")
                .email("user2@example.com")
                .phoneNumber("4445556666")
                .createdDate(OffsetDateTime.now())
                .lastUpdatedDate(OffsetDateTime.now())
                .build();

        userRepository.saveAllAndFlush(List.of(user1, user2));

        Chat chat = new Chat();
        chat.setIsGroup(false);
        chat.setUsers(Set.of(user1, user2));
        chatRepository.saveAndFlush(chat);

        Chat foundChat = chatRepository.findSingleChatByUsersId(user1, user2);

        assertNotNull(foundChat);
        assertFalse(foundChat.getIsGroup());
        assertEquals(2, foundChat.getUsers().size());
    }

    @Test
    void deleteChatsByIdTest() {
        Chat chat = new Chat();
        chatRepository.saveAndFlush(chat);

        chatRepository.deleteById(chat.getId());
        Optional<Chat> deletedChat = chatRepository.findById(chat.getId());

        assertFalse(deletedChat.isPresent());
    }
}
