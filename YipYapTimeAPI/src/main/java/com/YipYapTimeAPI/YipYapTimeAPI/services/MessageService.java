package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.ChatException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.MessageException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Message;
import com.YipYapTimeAPI.YipYapTimeAPI.request.SendMessageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {

    public Message sendMessage(SendMessageRequest req) throws UserException, ChatException;

    public List<Message> getChatsMessages(Integer chatId) throws ChatException;

    public Message findMessageById(Integer messageId) throws MessageException;

    public String deleteMessage(Integer messageId) throws MessageException;

}
