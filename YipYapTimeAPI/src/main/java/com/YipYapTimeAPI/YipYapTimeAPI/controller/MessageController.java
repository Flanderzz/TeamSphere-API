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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;


import java.util.List;

@RestController
@RequestMapping("/api/message")
@Slf4j
public class MessageController {

    private UserService userService;
    private MessageService messageService;
    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping("/create")
    public ResponseEntity<MessageDTO> sendMessageHandler(@RequestHeader("Authorization")String jwt, @RequestBody SendMessageRequest req) throws ChatException, UserException {

        try {
            log.info("Processing send message request for user with JWT: {}", jwt);

            User reqUser = userService.findUserProfile(jwt);

            req.setUserId(reqUser.getId());

            Message message = messageService.sendMessage(req);

            MessageDTO messageDto = MessageDTOMapper.toMessageDto(message);

            log.info("Message sent successfully by user with JWT: {}", jwt);

            return new ResponseEntity<>(messageDto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during send message process", e);
            throw new ChatException("Error during send message process" + e);
        }
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageDTO>> getChatsMessageHandler(@PathVariable Integer chatId) throws ChatException {
        try {
            log.info("Processing get messages for chat with ID: {}", chatId);

            List<Message> messages = messageService.getChatsMessages(chatId);

            List<MessageDTO> messageDtos = MessageDTOMapper.toMessageDtos(messages);

            log.info("Retrieved {} messages for chat with ID: {}", messageDtos.size(), chatId);

            return new ResponseEntity<>(messageDtos, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Error during get messages process for chat with ID: {}", chatId, e);
            throw new ChatException("Error during get messages process" + e);
        }
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse> deleteMessageHandler(@PathVariable Integer messageId) throws MessageException {
        try {
            log.info("Processing delete message request for message with ID: {}", messageId);

            messageService.deleteMessage(messageId);

            log.info("Message with ID {} deleted successfully", messageId);

            ApiResponse res = new ApiResponse("Message deleted", true);

            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Error during delete message process for message with ID: {}", messageId, e);
            throw new MessageException("Error during delete message process" + e);
        }
    }

}
