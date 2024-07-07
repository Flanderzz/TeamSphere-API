package com.YipYapTimeAPI.YipYapTimeAPI.controller;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.ChatDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper.ChatDTOMapper;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.ChatException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.request.GroupChatRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.request.RenameGroupChatRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.request.SingleChatRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.services.ChatService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    private final UserService userService;
    
    private final ChatDTOMapper chatDTOMapper;

    public ChatController(ChatService chatService, UserService userService, ChatDTOMapper chatDTOMapper) {
        this.chatService = chatService;
        this.userService = userService;
        this.chatDTOMapper = chatDTOMapper;
    }

    @PostMapping("/single")
    public ResponseEntity<ChatDTO> creatChatHandler(@RequestBody SingleChatRequest singleChatRequest, @RequestHeader("Authorization")  String jwt) throws UserException {

        log.info("single chat --------");
        User reqUser = userService.findUserProfile(jwt);

        Chat chat = chatService.createChat(reqUser.getId(),singleChatRequest.getUserId(),false);
        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);
        return new ResponseEntity<>(chatDto, HttpStatus.OK);
    }

    @PostMapping("/group")
    public ResponseEntity<ChatDTO> createGroupHandler(@RequestBody GroupChatRequest groupChatRequest, @RequestHeader("Authorization") String jwt) throws UserException{

        User reqUser = userService.findUserProfile(jwt);

        Chat chat = chatService.createGroup(groupChatRequest, reqUser.getId());
        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);

    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDTO> findChatByIdHandler(@PathVariable UUID chatId) throws ChatException {

        Chat chat = chatService.findChatById(chatId);

        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);

    }

    @GetMapping("/user")
    public ResponseEntity<List<ChatDTO>> findAllChatByUserIdHandler(@RequestHeader("Authorization")String jwt) throws UserException{

        User user = userService.findUserProfile(jwt);

        List<Chat> chats = chatService.findAllChatByUserId(user.getId());

        List<ChatDTO> chatDtos = chatDTOMapper.toChatDtos(chats);

        return new ResponseEntity<>(chatDtos, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{chatId}/add/{userId}")
    public ResponseEntity<ChatDTO> addUserToGroupHandler(@PathVariable UUID chatId,@PathVariable UUID userId) throws UserException, ChatException{


        Chat chat = chatService.addUserToGroup(userId, chatId);

        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);
    }

    @PutMapping("/{chatId}/rename")
    public ResponseEntity<ChatDTO> renameGroupHandler(@PathVariable UUID chatId, @RequestBody RenameGroupChatRequest renameGroupRequest, @RequestHeader("Authorization") String jwt) throws ChatException, UserException{

        User reqUser = userService.findUserProfile(jwt);

        Chat chat = chatService.renameGroup(chatId, renameGroupRequest.getGroupName(), reqUser.getId());

        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);
    }

    @PutMapping("/{chatId}/remove/{userId}")
    public ResponseEntity<ChatDTO> removeFromGroupHandler(@RequestHeader("Authorization") String jwt, @PathVariable UUID chatId,@PathVariable UUID userId) throws UserException, ChatException{


        User reqUser=userService.findUserProfile(jwt);

        Chat chat = chatService.removeFromGroup(chatId, userId, reqUser.getId());

        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{chatId}/{userId}")
    public ResponseEntity<ChatDTO> deleteChatHandler(@PathVariable UUID chatId, @PathVariable UUID userId) throws ChatException, UserException{

        Chat chat = chatService.deleteChat(chatId, userId);
        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);
    }
}
