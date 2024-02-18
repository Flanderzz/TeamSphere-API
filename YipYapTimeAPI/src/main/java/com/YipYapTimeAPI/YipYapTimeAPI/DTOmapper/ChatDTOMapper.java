package com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.ChatDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTO.MessageDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTO.UserDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChatDTOMapper {

    public static ChatDTO toChatDto(Chat chat) {

        UserDTO userDto = UserDTOMapper.toUserDTO(chat.getCreated_by());

        List<MessageDTO> messageDtos = MessageDTOMapper.toMessageDtos(chat.getMessages());

        Set<UserDTO> userDtos = UserDTOMapper.toUserDtos(chat.getUsers());

        Set<UserDTO> admins = UserDTOMapper.toUserDtos(chat.getAdmins());

        ChatDTO chatDto = ChatDTO.builder()
                .id(chat.getId())
                .chat_image(chat.getChat_image())
                .chat_name(chat.getChat_name())
                .created_by(userDto)
                .is_group(chat.getIs_group())
                .messages(messageDtos)
                .users(userDtos)
                .admins(admins)
                .build();


        return chatDto;
    }

    public static List<ChatDTO > toChatDtos(List<Chat> chats){

        List<ChatDTO> chatDtos = new ArrayList<>();

        for(Chat chat:chats) {
            ChatDTO chatDto = toChatDto(chat);
            chatDtos.add(chatDto);
        }

        return chatDtos;
    }
}
