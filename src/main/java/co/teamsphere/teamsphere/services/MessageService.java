package co.teamsphere.teamsphere.services;

import co.teamsphere.teamsphere.exception.ChatException;
import co.teamsphere.teamsphere.exception.MessageException;
import co.teamsphere.teamsphere.exception.UserException;
import co.teamsphere.teamsphere.models.Messages;
import co.teamsphere.teamsphere.request.SendMessageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface MessageService {

    Messages sendMessage(SendMessageRequest req) throws UserException, ChatException;

    List<Messages> getChatsMessages(UUID chatId) throws ChatException;

    Messages findMessageById(UUID messageId) throws MessageException;

    void deleteMessage(UUID messageId) throws MessageException;

}
