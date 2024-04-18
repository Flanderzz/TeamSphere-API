package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.ChatException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.MessageException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Messages;
import com.YipYapTimeAPI.YipYapTimeAPI.request.SendMessageRequest;
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
