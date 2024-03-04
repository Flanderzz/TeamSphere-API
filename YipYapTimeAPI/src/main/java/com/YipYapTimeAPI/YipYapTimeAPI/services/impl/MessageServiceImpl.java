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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    private MessageRepository messageRepo;

    private UserService userService;

    private ChatService chatService;

    public MessageServiceImpl(MessageRepository messageRepo, UserService userService, ChatService chatService) {
        this.messageRepo = messageRepo;
        this.userService = userService;
        this.chatService = chatService;
    }

    @Override
    public Message sendMessage(SendMessageRequest req) throws UserException, ChatException {

        log.info("Attempting to send a message");

        try {
            User user = userService.findUserById(req.getUserId());
            log.info("Found user for sending message: {}", user);

            Chat chat = chatService.findChatById(req.getChatId());
            log.info("Found chat for sending message: {}", chat);

            Message message = Message.builder()
                .chat(chat)
                .username(user)
                .content(req.getContent())
                .timeStamp(LocalDateTime.now())
                .is_read(false)
                .build();

            log.info("Creating and saving the message: {}", message);

            return messageRepo.save(message);
        } catch (UserException | ChatException e) {
            log.error("Error sending message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error sending message", e);
            throw new ChatException("Error sending message" + e.getMessage());
        }
    }

    @Override
    public void deleteMessage(Long messageId) throws MessageException {
        log.info("Attempting to delete message with ID: {}", messageId);

        try {
            Message message = findMessageById(messageId);
            log.info("Found message for deletion: {}", message);

            messageRepo.deleteById(message.getId());
            log.info("Message deleted successfully");

        } catch (MessageException e) {
            log.error("Error deleting message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting message", e);
            throw new MessageException("Error deleting message: " + e.getMessage());
        }
    }

    @Override
    public List<Message> getChatsMessages(Integer chatId) throws ChatException {
        log.info("Attempting to retrieve messages for chat with ID: {}", chatId);

        try {
            Chat chat = chatService.findChatById(chatId);
            log.info("Found chat for retrieving messages: {}", chat);

            List<Message> messages = messageRepo.findMessageByChatId(chatId);
            log.info("Retrieved {} messages for chat with ID: {}", messages.size(), chatId);

            return messages;
        } catch (ChatException e) {
            log.error("Error retrieving messages: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error retrieving messages", e);
            throw new ChatException("Error retrieving messages: " + e.getMessage());
        }
    }

    @Override
    public Message findMessageById(Long messageId) throws MessageException {
        log.info("Attempting to find message by ID: {}", messageId);

        try {
            Optional<Message> optionalMessage = messageRepo.findById(messageId);

            if (optionalMessage.isPresent()) {
                Message message = optionalMessage.get();
                log.info("Found message for ID {}: {}", messageId, message);
                return message;
            }

            log.error("Message with ID {} not found. Unable to retrieve.", messageId);
            throw new MessageException("Message not found with ID: " + messageId);
        } catch (MessageException e) {
            log.error("Error finding message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error finding message", e);
            throw new MessageException("Error finding message: " + e.getMessage());
        }
    }

}
