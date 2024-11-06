package co.teamsphere.teamsphere.DTOmapper;

import co.teamsphere.teamsphere.DTO.ChatDTO;
import co.teamsphere.teamsphere.models.Chat;
import co.teamsphere.teamsphere.models.Messages;
import co.teamsphere.teamsphere.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ChatDTOMapper {
    @Mappings({
            @Mapping(source = "chatName", target = "chatName"),
            @Mapping(source = "chatImage", target = "chatImage"),
            @Mapping(source = "isGroup", target = "isGroup"),
            @Mapping(source = "admins", target = "admins", qualifiedByName = "usersToUserIds"),
            @Mapping(source = "createdBy.id", target = "createdBy"),
            @Mapping(source = "users", target = "users", qualifiedByName = "usersToUserIds"),
            @Mapping(source = "messages", target = "messages", qualifiedByName = "messagesToMessageIds")
    })
    ChatDTO toChatDto(Chat chat);

    List<ChatDTO> toChatDtos(List<Chat> chats);

    @Named("usersToUserIds")

    default Set<UUID> usersToUserIds(Set<User> users) {
        return users.stream().map(User::getId).collect(Collectors.toSet());
    }

    @Named("messagesToMessageIds")
    default List<UUID> messagesToMessageIds(List<Messages> messages) {
        return messages.stream().map(Messages::getId).collect(Collectors.toList());
    }

}
