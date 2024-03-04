package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.ChatException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.request.GroupChatRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ChatService{

    Chat createChat(UUID reqUserId, UUID userId2, boolean isGroup) throws UserException;

    Chat findChatById(Integer chatId) throws ChatException;

    List<Chat> findAllChatByUserId(UUID userId) throws UserException;

    Chat createGroup(GroupChatRequest req, UUID reqUerId) throws UserException;

    Chat addUserToGroup(UUID userId, Integer chatId) throws UserException, ChatException;

    Chat renameGroup(Integer chatId, String groupName, UUID reqUserId) throws ChatException, UserException;

    Chat removeFromGroup(Integer chatId, UUID userId, UUID reqUser) throws UserException,ChatException;

    Chat deleteChat(Integer chatId, UUID userId) throws ChatException, UserException;

}