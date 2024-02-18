package com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.ChatDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTO.MessageDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTO.UserDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageDTOMapper {

    public static MessageDTO toMessageDto(Message message) {

        ChatDTO chatDto = ChatDTOMapper.toChatDto(message.getChat());
        UserDTO userDto = UserDTOMapper.toUserDTO(message.getUsername());

        MessageDTO messageDTO = MessageDTO.builder()
                .id(message.getId())
                .chat(chatDto)
                .content(message.getContent())
                .is_read(message.getIs_read())
                .timeStamp(message.getTimeStamp())
                .user(userDto)
                .build();

        return messageDTO;
    }

    public static List<MessageDTO> toMessageDtos(List<Message> messages){

        List<MessageDTO> messageDtos = new ArrayList<>();

        for(Message message : messages) {
            MessageDTO messageDto = toMessageDto(message);
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

}
