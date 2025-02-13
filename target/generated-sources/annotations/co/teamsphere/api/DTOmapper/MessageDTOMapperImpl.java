package co.teamsphere.api.DTOmapper;

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
public class MessageDTOMapperImpl implements MessageDTOMapper {

    @Override
    public MessageDTO toMessageDto(Messages messages) {
        if ( messages == null ) {
            return null;
        }

        MessageDTO.MessageDTOBuilder messageDTO = MessageDTO.builder();

        messageDTO.id( messages.getId() );
        messageDTO.content( messages.getContent() );
        messageDTO.timeStamp( messages.getTimeStamp() );
        messageDTO.isRead( messages.getIsRead() );
        messageDTO.userId( messagesUsernameId( messages ) );
        messageDTO.chatId( messagesChatId( messages ) );

        return messageDTO.build();
    }

    @Override
    public List<MessageDTO> toMessageDtos(List<Messages> messages) {
        if ( messages == null ) {
            return null;
        }

        List<MessageDTO> list = new ArrayList<MessageDTO>( messages.size() );
        for ( Messages messages1 : messages ) {
            list.add( toMessageDto( messages1 ) );
        }

        return list;
    }

    private UUID messagesUsernameId(Messages messages) {
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

    private UUID messagesChatId(Messages messages) {
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
