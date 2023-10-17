package com.chatApp.webSocketAPI.service;

import com.chatApp.webSocketAPI.Exception.ChatException;
import com.chatApp.webSocketAPI.Exception.UserException;
import com.chatApp.webSocketAPI.model.Chat;
import com.chatApp.webSocketAPI.model.User;
import com.chatApp.webSocketAPI.request.GroupChatRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

    public Chat createChat(User requestedUserID, Integer userID) throws UserException;

    public Chat findChatByID(Integer chatID) throws ChatException;

    public Chat addUserToGroupChat (Integer userID, Integer chatID, User requestedUser) throws UserException, ChatException;

    public Chat createGroupChat(GroupChatRequest request, User requestedUserID) throws UserException;

    public Chat renameGroupChat (Integer chatID, String groupChatName, User requestedUserID) throws ChatException, UserException;

    public Chat removeUserFromGroupChat (Integer chatID, Integer userID, User reqUserID) throws ChatException, UserException;

    public void deleteChat (Integer chatID, Integer userID) throws ChatException, UserException;

    public List<Chat> findAllChatByUserID(Integer userID) throws UserException;
}
