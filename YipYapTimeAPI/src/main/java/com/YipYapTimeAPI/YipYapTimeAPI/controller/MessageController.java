package com.YipYapTimeAPI.YipYapTimeAPI.controller;

import com.YipYapTimeAPI.YipYapTimeAPI.DTO.MessageDTO;
import com.YipYapTimeAPI.YipYapTimeAPI.DTOmapper.MessageDTOMapper;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.ChatException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.MessageException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Message;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.request.SendMessageRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.response.ApiResponse;
import com.YipYapTimeAPI.YipYapTimeAPI.services.MessageService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private UserService userService;
    private MessageService messageService;

    @Autowired
    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping("/create")
    public ResponseEntity<MessageDTO> sendMessageHandler(@RequestHeader("Authorization")String jwt, @RequestBody SendMessageRequest req) throws ChatException, UserException {

        User reqUser=userService.findUserProfile(jwt);

        req.setUserId(reqUser.getId());

        Message message = messageService.sendMessage(req);

        MessageDTO messageDto= MessageDTOMapper.toMessageDto(message);

        return new ResponseEntity<MessageDTO>(messageDto, HttpStatus.OK);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageDTO>> getChatsMessageHandler(@PathVariable Integer chatId) throws ChatException{

        List<Message> messages = messageService.getChatsMessages(chatId);

        List<MessageDTO> messageDtos = MessageDTOMapper.toMessageDtos(messages);

        return new ResponseEntity<List<MessageDTO>>(messageDtos,HttpStatus.ACCEPTED);

    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse> deleteMessageHandler(@PathVariable Integer messageId) throws MessageException {

        messageService.deleteMessage(messageId);

        ApiResponse res = new ApiResponse("message deleted",true);

        return new ResponseEntity<ApiResponse>(res,HttpStatus.ACCEPTED);
    }

}
