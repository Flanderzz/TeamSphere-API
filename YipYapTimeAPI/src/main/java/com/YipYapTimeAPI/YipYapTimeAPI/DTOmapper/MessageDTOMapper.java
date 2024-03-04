package com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.ChatDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTO.MessageDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTO.UserDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Messages;

import java.util.ArrayList;
import java.util.List;

public class MessageDTOMapper {

    public static MessageDTO toMessageDto(Messages messages) {

        ChatDTO chatDto = ChatDTOMapper.toChatDto(messages.getChat());
        UserDTO userDto = UserDTOMapper.toUserDTO(messages.getUsername());

        MessageDTO messageDTO = MessageDTO.builder()
                .id(messages.getId())
                .chat(chatDto)
                .content(messages.getContent())
                .is_read(messages.getIs_read())
                .timeStamp(messages.getTimeStamp())
                .user(userDto)
                .build();

        return messageDTO;
    }

    public static List<MessageDTO> toMessageDtos(List<Messages> messages){

        List<MessageDTO> messageDtos = new ArrayList<>();

        for(Messages message : messages) {
            MessageDTO messageDto = toMessageDto(message);
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

}
