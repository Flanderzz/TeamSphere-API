package co.teamsphere.teamsphere.services;

import co.teamsphere.teamsphere.exception.ChatException;
import co.teamsphere.teamsphere.exception.UserException;
import co.teamsphere.teamsphere.models.Chat;
import co.teamsphere.teamsphere.request.GroupChatRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ChatService{

    Chat createChat(UUID reqUserId, UUID userId2, boolean isGroup) throws UserException;

    Chat findChatById(UUID chatId) throws ChatException;

    List<Chat> findAllChatByUserId(UUID userId) throws UserException;

    Chat createGroup(GroupChatRequest req, UUID reqUerId) throws UserException;

    Chat addUserToGroup(UUID userId, UUID chatId) throws UserException, ChatException;

    Chat renameGroup(UUID chatId, String groupName, UUID reqUserId) throws ChatException, UserException;

    Chat removeFromGroup(UUID chatId, UUID userId, UUID reqUser) throws UserException,ChatException;

    Chat deleteChat(UUID chatId, UUID userId) throws ChatException, UserException;

}