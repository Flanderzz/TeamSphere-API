package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.ChatException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.request.GroupChatRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService{

    Chat createChat(Integer reqUserId, Integer userId2, boolean isGroup) throws UserException;

    Chat findChatById(Integer chatId) throws ChatException;

    List<Chat> findAllChatByUserId(Integer userId) throws UserException;

    Chat createGroup(GroupChatRequest req, Integer reqUerId) throws UserException;

    Chat addUserToGroup(Integer userId, Integer chatId) throws UserException, ChatException;

    Chat renameGroup(Integer chatId, String groupName, Integer reqUserId) throws ChatException, UserException;

    Chat removeFromGroup(Integer chatId, Integer userId, Integer reqUser) throws UserException,ChatException;

    Chat deleteChat(Integer chatId, Integer userId) throws ChatException, UserException;

}