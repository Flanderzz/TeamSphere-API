package com.chatApp.webSocketAPI.controller;

import com.chatApp.webSocketAPI.Exception.ChatException;
import com.chatApp.webSocketAPI.Exception.UserException;
import com.chatApp.webSocketAPI.model.Chat;
import com.chatApp.webSocketAPI.model.User;
import com.chatApp.webSocketAPI.request.GroupChatRequest;
import com.chatApp.webSocketAPI.request.SingleChatRequest;
import com.chatApp.webSocketAPI.response.ApiResponse;
import com.chatApp.webSocketAPI.service.ChatService;
import com.chatApp.webSocketAPI.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private ChatService chatService;
    private UserService userService;
    public ChatController() {}

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @PostMapping("/single")
    public ResponseEntity<Chat> createChatHandler(@RequestBody SingleChatRequest singleChatRequest, @RequestHeader("Authorization") String jwt) throws UserException {
        User requestUser = userService.findUserProfile(jwt);

        Chat chat = chatService.createChat(requestUser, singleChatRequest.getUserID());
        return new ResponseEntity<Chat>(chat, OK);
    }

    @PostMapping("/groupchat")
    public ResponseEntity<Chat> createGroupChatHandler(@RequestBody GroupChatRequest groupChatRequest, @RequestHeader("Authorization") String jwt) throws UserException {
        User requestUser = userService.findUserProfile(jwt);

        Chat chat = chatService.createGroupChat(groupChatRequest, requestUser);
        return new ResponseEntity<Chat>(chat, OK);
    }

    @GetMapping("/{chatID}")
    public ResponseEntity<Chat> findChatByIDHandler(@PathVariable Integer chatID, @RequestHeader("Authorization") String jwt) throws ChatException {
        Chat chat = chatService.findChatByID(chatID);
        return new ResponseEntity<Chat>(chat, OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Chat>> findAllChatByUserIDHandler(@RequestHeader("Authorization") String jwt) throws ChatException, UserException {
        User requestUser = userService.findUserProfile(jwt);
        List<Chat> chats = chatService.findAllChatByUserID(requestUser.getID());
        return new ResponseEntity<List<Chat>>(chats, OK);
    }

    @PutMapping("/{chatID}/add/{userID}")
    public ResponseEntity<Chat> addUserToGroupChatHandler(@PathVariable Integer chatID, @PathVariable Integer userID, @RequestHeader("Authorization") String jwt) throws ChatException, UserException {
        User requestUser = userService.findUserProfile(jwt);
        Chat chat = chatService.addUserToGroupChat(chatID, userID, requestUser);
        return new ResponseEntity<>(chat, OK);
    }

    @PutMapping("/{chatID}/remove/{userID}")
    public ResponseEntity<Chat> removeUserToGroupChatHandler(@PathVariable Integer chatID, @PathVariable Integer userID, @RequestHeader("Authorization") String jwt) throws ChatException, UserException {
        User requestUser = userService.findUserProfile(jwt);
        Chat chat = chatService.removeUserFromGroupChat(chatID, userID, requestUser);
        return new ResponseEntity<>(chat, OK);
    }

    @DeleteMapping("/delete/{chatID}")
    public ResponseEntity<ApiResponse> deleteChatHandler(@PathVariable Integer chatID, @RequestHeader("Authorization") String jwt) throws ChatException, UserException {
        User requestUser = userService.findUserProfile(jwt);
        chatService.deleteChat(chatID, requestUser.getID());
        ApiResponse apiResponse = new ApiResponse("Chat Deleted", true);
        return new ResponseEntity<>(apiResponse, OK);
    }
}
