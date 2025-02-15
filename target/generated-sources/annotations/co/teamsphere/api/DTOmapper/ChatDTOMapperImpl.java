package co.teamsphere.api.DTOmapper;

import co.teamsphere.api.DTO.ChatDTO;
import co.teamsphere.api.DTO.MessageDTO;
import co.teamsphere.api.models.Chat;
import co.teamsphere.api.models.Messages;
import co.teamsphere.api.models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-11T20:03:08+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ChatDTOMapperImpl implements ChatDTOMapper {

    @Override
    public ChatDTO toChatDto(Chat chat) {
        if ( chat == null ) {
            return null;
        }

        ChatDTO.ChatDTOBuilder chatDTO = ChatDTO.builder();

        chatDTO.chatName( chat.getChatName() );
        chatDTO.chatImage( chat.getChatImage() );
        chatDTO.isGroup( chat.getIsGroup() );
        chatDTO.admins( usersToUserIds( chat.getAdmins() ) );
        chatDTO.createdBy( chatCreatedById( chat ) );
        chatDTO.users( usersToUserIds( chat.getUsers() ) );
        chatDTO.messages( messagesToMessageIds( chat.getMessages() ) );
        chatDTO.id( chat.getId() );

        return chatDTO.build();
    }

    @Override
    public List<ChatDTO> toChatDtos(List<Chat> chats) {
        if ( chats == null ) {
            return null;
        }

        List<ChatDTO> list = new ArrayList<ChatDTO>( chats.size() );
        for ( Chat chat : chats ) {
            list.add( toChatDto( chat ) );
        }

        return list;
    }

    @Override
    public MessageDTO toMessageDto(Messages message) {
        if ( message == null ) {
            return null;
        }

        MessageDTO.MessageDTOBuilder messageDTO = MessageDTO.builder();

        messageDTO.id( message.getId() );
        messageDTO.content( message.getContent() );
        messageDTO.timeStamp( message.getTimeStamp() );
        messageDTO.isRead( message.getIsRead() );
        messageDTO.userId( messageUsernameId( message ) );
        messageDTO.chatId( messageChatId( message ) );

        return messageDTO.build();
    }

    private UUID chatCreatedById(Chat chat) {
        if ( chat == null ) {
            return null;
        }
        User createdBy = chat.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        UUID id = createdBy.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID messageUsernameId(Messages messages) {
        if ( messages == null ) {
            return null;
        }
        User username = messages.getUsername();
        if ( username == null ) {
            return null;
        }
        UUID id = username.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID messageChatId(Messages messages) {
        if ( messages == null ) {
            return null;
        }
        Chat chat = messages.getChat();
        if ( chat == null ) {
            return null;
        }
        UUID id = chat.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
