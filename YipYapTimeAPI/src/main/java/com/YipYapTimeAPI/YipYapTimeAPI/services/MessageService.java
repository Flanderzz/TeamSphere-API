package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.ChatException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.MessageException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Messages;
import com.YipYapTimeAPI.YipYapTimeAPI.request.SendMessageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {

    Messages sendMessage(SendMessageRequest req) throws UserException, ChatException;

    List<Messages> getChatsMessages(Integer chatId) throws ChatException;

    Messages findMessageById(Long messageId) throws MessageException;

    void deleteMessage(Long messageId) throws MessageException;

}
