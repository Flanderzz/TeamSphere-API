package co.teamsphere.teamsphere.controller;

import co.teamsphere.teamsphere.DTO.MessageDTO;
import co.teamsphere.teamsphere.DTOmapper.MessageDTOMapper;
import co.teamsphere.teamsphere.exception.ChatException;
import co.teamsphere.teamsphere.exception.MessageException;
import co.teamsphere.teamsphere.models.Messages;
import co.teamsphere.teamsphere.models.User;
import co.teamsphere.teamsphere.request.SendMessageRequest;
import co.teamsphere.teamsphere.response.ApiResponse;
import co.teamsphere.teamsphere.services.MessageService;
import co.teamsphere.teamsphere.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@Slf4j
public class MessageController {
    private final MessageDTOMapper messageDTOMapper;
    private final UserService userService;
    private final MessageService messageService;
    public MessageController(UserService userService, MessageService messageService, MessageDTOMapper messageDTOMapper) {
        this.userService = userService;
        this.messageService = messageService;
        this.messageDTOMapper = messageDTOMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<MessageDTO> sendMessageHandler(@RequestHeader("Authorization")String jwt, @RequestBody SendMessageRequest req) throws ChatException {

        try {
            log.info("Processing send message request to userId: {}", req.getUserId());

            User reqUser = userService.findUserProfile(jwt);
            req.setUserId(reqUser.getId());

            Messages messages = messageService.sendMessage(req);

            MessageDTO messageDto = messageDTOMapper.toMessageDto(messages);

            log.info("Message sent successfully by userId: {}", reqUser.getId());

            return new ResponseEntity<>(messageDto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during send message process", e);
            throw new ChatException("Error during send message process" + e);
        }
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<MessageDTO>> getChatsMessageHandler(@PathVariable UUID chatId) throws ChatException {
        try {
            log.info("Processing get messages for chat with ID: {}", chatId);

            List<Messages> messages = messageService.getChatsMessages(chatId);

            List<MessageDTO> messageDtos = messageDTOMapper.toMessageDtos(messages);

            log.info("Retrieved {} messages for chat with ID: {}", messageDtos.size(), chatId);

            return new ResponseEntity<>(messageDtos, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Error during get messages process for chat with ID: {}", chatId, e);
            throw new ChatException("Error during get messages process" + e);
        }
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse> deleteMessageHandler(@PathVariable UUID messageId) throws MessageException {
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
