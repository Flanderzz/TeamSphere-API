package com.YipYapTimeAPI.YipYapTimeAPI.services.impl;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.ChatException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.MessageException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Message;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.MessageRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.request.SendMessageRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.services.ChatService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.MessageService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    public MessageServiceImpl(MessageRepository messageRepo, UserService userService, ChatService chatService) {
        this.messageRepo = messageRepo;
        this.userService = userService;
        this.chatService = chatService;
    }

    @Override
    public Message sendMessage(SendMessageRequest req) throws UserException, ChatException {

        System.out.println("send message ------- ");

        User user=userService.findUserById(req.getUserId());
        Chat chat=chatService.findChatById(req.getChatId());

        Message message=new Message();
        message.setChat(chat);
        message.setUsername(user);
        message.setContent(req.getContent());
        message.setTimeStamp(LocalDateTime.now());
        message.setIs_read(false);


        return messageRepo.save(message);
    }

    @Override
    public String deleteMessage(Integer messageId) throws MessageException {

        Message message=findMessageById(messageId);

        messageRepo.deleteById(message.getId());

        return "message deleted successfully";
    }

    @Override
    public List<Message> getChatsMessages(Integer chatId) throws ChatException {

        Chat chat=chatService.findChatById(chatId);

        List<Message> messages=messageRepo.findMessageByChatId(chatId);

        return messages;
    }

    @Override
    public Message findMessageById(Integer messageId) throws MessageException {

        Optional<Message> message =messageRepo.findById(messageId);

        if(message.isPresent()) {
            return message.get();
        }
        throw new MessageException("message not exist with id "+messageId);
    }

}
