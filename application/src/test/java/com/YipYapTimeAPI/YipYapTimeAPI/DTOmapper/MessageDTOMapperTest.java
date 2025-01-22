package com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.MessageDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.helpers.TestDataBuilder;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Messages;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
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
@ContextConfiguration(classes = {MessageDTOMapperImpl.class})
public class MessageDTOMapperTest {

    @Autowired
    private MessageDTOMapper mapper;

    @Test
    public void testToMessageDto() {
        User testUser1 = TestDataBuilder.buildUser("user1", "profilePic1", "password1", "user1@example.com");

        Set<User> users1 = new HashSet<>();
        users1.add(testUser1);

        Chat chat1 = TestDataBuilder.buildChat("testChat1", "testImage1", true, users1);
        Messages message = new Messages();
        message.setId(UUID.randomUUID());
        message.setContent("Sample Message");
        message.setChat(chat1);
        message.setUsername(testUser1);
        MessageDTO messageDto = mapper.toMessageDto(message);

        assertThat(messageDto).isNotNull();

        assertThat(messageDto).isNotNull();
        assertThat(messageDto.getId()).isEqualTo(message.getId());
        assertThat(messageDto.getContent()).isEqualTo(message.getContent());

        // Assert Chat details in MessageDTO
        assertThat(messageDto.getChatId()).isNotNull();
        assertThat(messageDto.getChatId()).isEqualTo(chat1.getId());
    }

    @Test
    public void testToMessageDtos() {
        // Prepare test data
        User testUser1 = TestDataBuilder.buildUser("user1", "profilePic1", "password1", "user1@example.com");
        User testUser2 = TestDataBuilder.buildUser("user2", "profilePic2", "password2", "user2@example.com");

        Set<User> users1 = new HashSet<>();
        users1.add(testUser1);

        Set<User> users2 = new HashSet<>();
        users2.add(testUser2);

        Chat chat1 = TestDataBuilder.buildChat("testChat1", "testImage1", true, users1);
        Chat chat2 = TestDataBuilder.buildChat("testChat2", "testImage2", false, users2);

        Messages message1 = TestDataBuilder.buildMessage("Sample Message 1", chat1, testUser1);
        Messages message2 = TestDataBuilder.buildMessage("Sample Message 2", chat2, testUser2);

        List<Messages> messages = Arrays.asList(message1, message2);

        // Map List<Messages> to List<MessageDTO>
        List<MessageDTO> messageDtos = mapper.toMessageDtos(messages);

        // Assertions to verify the fields
        assertThat(messageDtos).isNotNull();
        assertThat(messageDtos).hasSize(2);

        // Verify first MessageDTO
        MessageDTO messageDto1 = messageDtos.get(0);
        assertThat(messageDto1).isNotNull();
        assertThat(messageDto1.getId()).isEqualTo(message1.getId());
        assertThat(messageDto1.getContent()).isEqualTo(message1.getContent());

        // Assert Chat details in MessageDTO 1
        assertThat(messageDto1.getChatId()).isNotNull();
        assertThat(messageDto1.getChatId()).isEqualTo(chat1.getId());

        // Verify second MessageDTO
        MessageDTO messageDto2 = messageDtos.get(1);
        assertThat(messageDto2).isNotNull();
        assertThat(messageDto2.getId()).isEqualTo(message2.getId());
        assertThat(messageDto2.getContent()).isEqualTo(message2.getContent());

        // Assert Chat details in MessageDTO 2
        assertThat(messageDto2.getChatId()).isNotNull();
        assertThat(messageDto2.getChatId()).isEqualTo(chat2.getId());
    }
}

