package co.teamsphere.teamsphere.DTOmapper;

import co.teamsphere.teamsphere.DTO.MessageDTO;
import co.teamsphere.teamsphere.models.Messages;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageDTOMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "content", target = "content"),
            @Mapping(source = "timeStamp", target = "timeStamp"),
            @Mapping(source = "isRead", target = "isRead"),
            @Mapping(source = "username.id", target = "userId"),
            @Mapping(source = "chat.id", target = "chatId")
    })
    MessageDTO toMessageDto(Messages messages);
    List<MessageDTO> toMessageDtos(List<Messages> messages);
}
