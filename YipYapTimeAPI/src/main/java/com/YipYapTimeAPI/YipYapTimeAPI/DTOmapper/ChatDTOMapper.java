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

        UserDTO userDto=UserDTOMapper.toUserDTO(chat.getCreated_by());
        List<MessageDTO> messageDtos=MessageDTOMapper.toMessageDtos(chat.getMessages());
        Set<UserDTO> userDtos=UserDTOMapper.toUserDtos(chat.getUsers());
        Set<UserDTO> admins=UserDTOMapper.toUserDtos(chat.getAdmins());

        ChatDTO chatDto=new ChatDTO();
        chatDto.setId(chat.getId());
        chatDto.setChat_image(chat.getChat_image());
        chatDto.setChat_name(chat.getChat_name());
        chatDto.setCreated_by(userDto);
        chatDto.setIs_group(chat.getIs_group());
        chatDto.setMessages(messageDtos);
        chatDto.setUsers(userDtos);
        chatDto.setAdmins(admins);

        return chatDto;
    }

    public static List<ChatDTO > toChatDtos(List<Chat> chats){

        List<ChatDTO> chatDtos = new ArrayList<>();

        for(Chat chat:chats) {
            ChatDTO chatDto=toChatDto(chat);
            chatDtos.add(chatDto);
        }

        return chatDtos;
    }
}
