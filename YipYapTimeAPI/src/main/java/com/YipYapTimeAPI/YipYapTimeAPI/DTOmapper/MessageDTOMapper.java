package com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.ChatDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTO.MessageDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTO.UserDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageDTOMapper {

    public static MessageDTO toMessageDto(Message message) {

        ChatDTO chatDto=ChatDTOMapper.toChatDto(message.getChat());
        UserDTO userDto=UserDTOMapper.toUserDTO(message.getUsername());

        MessageDTO messageDto=new MessageDTO();
        messageDto.setId(message.getId());
        messageDto.setChat(chatDto);
        messageDto.setContent(message.getContent());
        messageDto.setIs_read(message.getIs_read());
        messageDto.setTimeStamp(message.getTimeStamp());
        messageDto.setUser(userDto);

        return messageDto;
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
