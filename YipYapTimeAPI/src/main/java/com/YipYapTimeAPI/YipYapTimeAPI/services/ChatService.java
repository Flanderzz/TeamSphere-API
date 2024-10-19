package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.ChatSummaryDTO;
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

    Chat findChatById(UUID chatId) throws ChatException;

    Chat createGroup(GroupChatRequest req, UUID reqUerId) throws UserException;

    Chat addUserToGroup(UUID userId, UUID chatId) throws UserException, ChatException;

    Chat renameGroup(UUID chatId, String groupName, UUID reqUserId) throws ChatException, UserException;

    Chat removeFromGroup(UUID chatId, UUID userId, UUID reqUser) throws UserException,ChatException;

    Chat deleteChat(UUID chatId, UUID userId) throws ChatException, UserException;

    List<ChatSummaryDTO> getChatSummaries(UUID userId, int page, int size) throws ChatException;

}