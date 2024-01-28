package com.YipYapTimeAPI.YipYapTimeAPI.controller;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.ChatException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Message;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.request.SendMessageRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.services.ChatService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.MessageService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Iterator;

public class RealTimeMsgController {

    private SimpMessagingTemplate simpMessagingTemplate;

    private UserService userService;

    private MessageService messageService;

    private ChatService chatService;

    public RealTimeMsgController(SimpMessagingTemplate simpMessagingTemplate, UserService userService, MessageService messageService, ChatService chatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userService = userService;
        this.messageService = messageService;
        this.chatService = chatService;
    }

    @MessageMapping("/message")
    @SendTo("/group/public")
    public Message receiveMessage(@Payload Message message){

        simpMessagingTemplate.convertAndSend("/group/"+message.getChat().getId().toString(), message);

        return message;
    }

    @MessageMapping("/chat/{groupId}")
    public Message sendToUser(@Payload SendMessageRequest req, @Header("Authorization") String jwt, @DestinationVariable String groupId) throws ChatException, UserException {
        User user = userService.findUserProfile(jwt);
        req.setUserId(user.getId());

        Chat chat = chatService.findChatById(req.getChatId());

        Message createdMessage = messageService.sendMessage(req);

        User reciverUser = receiver(chat, user);

        simpMessagingTemplate.convertAndSendToUser(groupId, "/private", createdMessage);

        return createdMessage;
    }

    public User receiver(Chat chat, User reqUser) {
        Iterator<User> iterator = chat.getUsers().iterator();

        User user1 = iterator.next(); // get first user
        User user2 = iterator.next(); // get second user

        if(user1.getId().equals(reqUser.getId())){
            return user2;
        }
        return user1;
    }
}
