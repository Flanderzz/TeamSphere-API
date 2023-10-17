package com.chatApp.webSocketAPI.service;

import com.chatApp.webSocketAPI.Exception.ChatException;
import com.chatApp.webSocketAPI.Exception.MessageException;
import com.chatApp.webSocketAPI.Exception.UserException;
import com.chatApp.webSocketAPI.model.Message;
import com.chatApp.webSocketAPI.model.User;
import com.chatApp.webSocketAPI.request.SendMessageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {

    public Message sendMessageRequest(SendMessageRequest sendMessage) throws UserException, ChatException;

    public List<Message> getChatMessages(Integer chatID, User userReq) throws ChatException, UserException;

    public Message findMessageByID(Integer messageID) throws MessageException;

    public void deleteMessage(Integer messageID, User userReq) throws MessageException;

}
