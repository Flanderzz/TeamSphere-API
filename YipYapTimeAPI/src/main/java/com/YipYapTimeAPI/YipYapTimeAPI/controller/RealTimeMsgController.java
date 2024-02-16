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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;

@RequestMapping("/api/send")
@RestController
public class RealTimeMsgController {
    private Binding groupBinding;
    private Binding privateBinding;
    private RabbitTemplate rabbitTemplate;
    private UserService userService;
    private MessageService messageService;
    private ChatService chatService;

    public RealTimeMsgController(RabbitTemplate rabbitTemplate,
                                 UserService userService,
                                 MessageService messageService,
                                 ChatService chatService,
                                 Binding groupBinding,
                                 Binding privateBinding
                                 )
    {
        this.rabbitTemplate = rabbitTemplate;
        this.userService = userService;
        this.messageService = messageService;
        this.chatService = chatService;
        this.groupBinding = groupBinding;
        this.privateBinding = privateBinding;
    }

    @PostMapping("/message")
    public ResponseEntity<String> receiveMessage(@RequestBody Message message) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String payload = mapper.writeValueAsString(message);

        org.springframework.messaging.Message<String> channelMessage = MessageBuilder
                .withPayload(payload)
                .setHeader(MessageHeaders.CONTENT_TYPE, "Group_message")
                .setHeader("MESSAGE_ID", message.getId())
                .build();

        rabbitTemplate.convertAndSend(groupBinding.getExchange(),groupBinding.getRoutingKey(), channelMessage);

        return ResponseEntity.ok("Message sent: " + message);
    }

    @PostMapping("/chat/{groupId}")
    public ResponseEntity<String> sendToUser(@Payload SendMessageRequest req, @Header("Authorization") String jwt, @DestinationVariable String groupId) throws ChatException, UserException {
        User user = userService.findUserProfile(jwt);
        req.setUserId(user.getId());

        Chat chat = chatService.findChatById(req.getChatId());

        Message createdMessage = messageService.sendMessage(req);

        User reciverUser = receiver(chat, user);

        rabbitTemplate.convertAndSend(privateBinding.getExchange(), privateBinding.getRoutingKey(), createdMessage);

        return ResponseEntity.ok("Message sent: " + createdMessage);
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
