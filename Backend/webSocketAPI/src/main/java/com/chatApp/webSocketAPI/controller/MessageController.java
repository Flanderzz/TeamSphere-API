package com.chatApp.webSocketAPI.controller;

import com.chatApp.webSocketAPI.Exception.ChatException;
import com.chatApp.webSocketAPI.Exception.MessageException;
import com.chatApp.webSocketAPI.Exception.UserException;
import com.chatApp.webSocketAPI.model.Message;
import com.chatApp.webSocketAPI.model.User;
import com.chatApp.webSocketAPI.request.SendMessageRequest;
import com.chatApp.webSocketAPI.response.ApiResponse;
import com.chatApp.webSocketAPI.service.MessageService;
import com.chatApp.webSocketAPI.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private UserService userService;
    private MessageService messageService;

    public MessageController() {}
    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping("/create")
    public ResponseEntity<Message> sendMessageHandler(@RequestBody SendMessageRequest messageRequest, @RequestHeader("Authorization") String jwt) throws UserException, ChatException {
        User user = userService.findUserProfile(jwt);
        messageRequest.setUserID(user.getID());
        Message message = messageService.sendMessageRequest(messageRequest);

        return new ResponseEntity<Message> (message, OK);
    }

    @GetMapping("/chat/{chatID}")
    public ResponseEntity<List<Message>> getChatMessagesHandler(@PathVariable Integer chatID, @RequestHeader("Authorization") String jwt) throws UserException, ChatException {
        User user = userService.findUserProfile(jwt);
        List<Message> message = messageService.getChatMessages(chatID, user);

        return new ResponseEntity<> (message, OK);
    }

    @DeleteMapping("/{messageID}")
    public ResponseEntity<ApiResponse> deleteMessagesHandler(@PathVariable Integer messageID, @RequestHeader("Authorization") String jwt) throws MessageException, UserException {
        User user = userService.findUserProfile(jwt);
        messageService.deleteMessage(messageID, user);

        ApiResponse apiResponse = new ApiResponse("Message Delete", true);

        return new ResponseEntity<> (apiResponse, OK);
    }

}
