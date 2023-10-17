package com.chatApp.webSocketAPI.service.Impl;

import com.chatApp.webSocketAPI.Exception.ChatException;
import com.chatApp.webSocketAPI.Exception.MessageException;
import com.chatApp.webSocketAPI.Exception.UserException;
import com.chatApp.webSocketAPI.model.Chat;
import com.chatApp.webSocketAPI.model.Message;
import com.chatApp.webSocketAPI.repository.ChatRepository;
import com.chatApp.webSocketAPI.repository.MessageRepository;
import com.chatApp.webSocketAPI.repository.UserRepository;
import com.chatApp.webSocketAPI.request.SendMessageRequest;
import com.chatApp.webSocketAPI.service.ChatService;
import com.chatApp.webSocketAPI.service.MessageService;
import com.chatApp.webSocketAPI.service.UserService;
import com.chatApp.webSocketAPI.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;
    private UserService userService;
    private ChatService chatService;

    public MessageServiceImpl(MessageRepository messageRepository, UserService userService, ChatService chatService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.chatService = chatService;
    }

    @Override
    public Message sendMessageRequest(SendMessageRequest sendMessage) throws UserException, ChatException {
        User user = userService.findByID(sendMessage.getUserID());
        Chat chat = chatService.findChatByID(sendMessage.getChatID());

        Message message = new Message();
        message.setChat(chat);
        message.setUser(user);
        message.setContent(sendMessage.getContent());
        message.setTimeStamp(LocalDateTime.now());

        return message;
    }

    @Override
    public List<Message> getChatMessages(Integer chatID, User userReq) throws ChatException, UserException {
        Chat chat = chatService.findChatByID(chatID);
        if(chat.getUsers().contains(userReq)){
            throw new UserException("You Cant Do that here: No Perm: "+userReq.getID());
        }
        List<Message> messages = messageRepository.findChatMsgByID(chat.getID());

        return messages;
    }

    @Override
    public Message findMessageByID(Integer messageID) throws MessageException {
        Optional<Message> options = messageRepository.findById(messageID);

        if (options.isPresent()){
            return options.get();
        }

        throw new MessageException("Message Not Found By ID: "+messageID+ "!");
    }

    @Override
    public void deleteMessage(Integer messageID, User userReq) throws MessageException {
        Message message = findMessageByID(messageID);

        if (message.getUser().getID().equals(userReq.getID())) {
            messageRepository.deleteById(messageID);
        }
        throw new MessageException("Message Cannot Be Deleted By You" +userReq.getName()+ "!");
    }
}
