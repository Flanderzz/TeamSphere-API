package com.YipYapTimeAPI.YipYapTimeAPI.services.impl;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.ChatException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.ChatRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.request.GroupChatRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.services.ChatService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final UserService userService;

    private final ChatRepository chatRepository;

    public ChatServiceImpl(UserService userService, ChatRepository chatRepository) {
        this.userService = userService;
        this.chatRepository = chatRepository;
    }

    @Override
    public Chat createChat(UUID reqUserId, UUID userId2, boolean isGroup) throws UserException {
        try {
            log.info("Creating chat. reqUserId: {}, userId2: {}, isGroup: {}", reqUserId, userId2, isGroup);

            User reqUser = userService.findUserById(reqUserId);
            User user2 = userService.findUserById(userId2);

            Chat isChatExist = chatRepository.findSingleChatByUsersId(user2, reqUser);

            if (isChatExist != null) {
                log.info("Chat already exists for users: {} and {}", reqUserId, userId2);
                return isChatExist;
            }

            Chat chat = new Chat();

            chat.setCreatedBy(reqUser);
            chat.getUsers().add(reqUser);
            chat.getUsers().add(user2);
            chat.setIsGroup(isGroup);

            Chat createdChat = chatRepository.save(chat);

            log.info("Chat created successfully. Chat: {}", createdChat);

            return createdChat;
        } catch (Exception e) {
            log.error("Error creating chat", e);
            throw new UserException("Error creating chat" + e);
        }
    }

    @Override
    public Chat findChatById(UUID chatId) throws ChatException {
        try {
            log.info("Attempting to find chat by ID: {}", chatId);

            Optional<Chat> chat = chatRepository.findById(chatId);

            if (chat.isPresent()) {
                log.info("Chat found with ID: {}", chatId);
                return chat.get();
            }

            log.info("Chat not found with ID: {}", chatId);
            throw new ChatException("Chat not exist with ID " + chatId);
        } catch (Exception e) {
            log.error("Error finding chat by ID: {}", chatId, e);
            throw new ChatException("Error finding chat by ID: " + chatId + e);
        }
    }

    @Override
    public List<Chat> findAllChatByUserId(UUID userId) throws UserException {
        try {
            log.info("Finding all chats for user with ID: {}", userId);

            User user = userService.findUserById(userId);

            List<Chat> chats = chatRepository.findChatByUserId(user.getId());

            log.info("Found {} chats for user with ID: {}", chats.size(), userId);

            return chats;
        } catch (Exception e) {
            log.error("Error finding chats for user with ID: {}", userId, e);
            throw new UserException("Error finding chats for user with ID: " + userId + e);
        }
    }

    @Override
    public Chat deleteChat(UUID chatId, UUID userId) throws ChatException, UserException {
        try {
            log.info("Attempting to delete chat with ID: {} by user with ID: {}", chatId, userId);

            User user = userService.findUserById(userId);
            Chat chat = findChatById(chatId);

            // Check if the user has permission to delete the chat
            if (chat.getCreatedBy().getId().equals(user.getId()) && !chat.getIsGroup()) {
                chatRepository.deleteById(chat.getId());
                log.info("Chat deleted successfully. Chat ID: {}, User ID: {}", chatId, userId);
                return chat;
            }

            // If user does not have permission or chat is a group chat, throw an exception
            throw new ChatException("You don't have permission to delete this chat or the chat is a group chat");
        } catch (UserException | ChatException e) {
            log.error("Error deleting chat with ID: {}. {}", chatId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting chat with ID: {}. {}", chatId, e.getMessage(), e);
            throw new ChatException("Error deleting chat with ID: " + chatId + e);
        }
    }


    @Override
    public Chat createGroup(GroupChatRequest req, UUID reqUserId) throws UserException {
        try {
            log.info("Creating group chat. Requested by user with ID: {}", reqUserId);

            User reqUser = userService.findUserById(reqUserId);

            Chat chat = new Chat();

            chat.setCreatedBy(reqUser);
            chat.getUsers().add(reqUser);

            for (UUID userId : req.getUserIds()) {
                User user = userService.findUserById(userId);
                if (user != null) {
                    chat.getUsers().add(user);
                    log.info("Added user with ID {} to the group chat", userId);
                } else {
                    log.warn("User with ID {} not found while creating the group chat", userId);
                }
            }

            //TODO: Add builder pattern here
//            chat.builder().chat_name(req.getChat_name())
////                    .chat_image(req.getChat_image())
////                    .is_group(true)
////                    .admins(reqUser
////                    .build();
            chat.setChatName(req.getChat_name());
            chat.setChatImage(req.getChat_image());
            chat.setIsGroup(true);
            chat.getAdmins().add(reqUser);

            Chat createdChat = chatRepository.save(chat);

            log.info("Group chat created successfully. Chat ID: {}", createdChat.getId());

            return createdChat;
        } catch (Exception e) {
            log.error("Error creating group chat", e);
            throw new UserException("Error creating group chat" + e);
        }
    }


    @Override
    public Chat addUserToGroup(UUID userId, UUID chatId) throws UserException {
        try {
            log.info("Adding user with ID {} to group chat with ID: {}", userId, chatId);

            Chat chat = findChatById(chatId);
            User user = userService.findUserById(userId);

            chat.getUsers().add(user);

            Chat updatedChat = chatRepository.save(chat);

            log.info("User with ID {} added to group chat successfully. Updated chat ID: {}", userId, chatId);

            return updatedChat;
        } catch (Exception e) {
            log.error("Error adding user with ID {} to group chat with ID: {}", userId, chatId, e);
            throw new UserException("Error adding user to group chat" + e);
        }
    }

    @Override
    public Chat renameGroup(UUID chatId, String groupName, UUID reqUserId) throws UserException {
        try {
            log.info("Renaming group chat with ID: {} to: {} by user with ID: {}", chatId, groupName, reqUserId);

            Chat chat = findChatById(chatId);
            User user = userService.findUserById(reqUserId);

            if (chat.getUsers().contains(user)) {
                chat.setChatName(groupName);
                log.info("Group chat renamed successfully to: {}", groupName);
            } else {
                log.warn("User with ID {} doesn't have permission to rename group chat with ID: {}", reqUserId, chatId);
            }

            return chatRepository.save(chat);
        } catch (Exception e) {
            log.error("Error renaming group chat with ID: {} by user with ID: {}", chatId, reqUserId, e);
            throw new UserException("Error renaming group chat" + e);
        }
    }

    @Override
    public Chat removeFromGroup(UUID chatId, UUID userId, UUID reqUserId) throws UserException {
        try {
            log.info("Removing user with ID {} from group chat with ID: {} by user with ID: {}", userId, chatId, reqUserId);

            Chat chat = findChatById(chatId);
            User user = userService.findUserById(userId);
            User reqUser = userService.findUserById(reqUserId);

            if (user.getId().equals(reqUser.getId())) {
                chat.getUsers().remove(reqUser);
                log.info("User with ID {} removed from group chat successfully. Updated chat ID: {}", userId, chatId);
            } else {
                log.warn("User with ID {} doesn't have permission to remove user with ID {} from group chat with ID: {}", reqUserId, userId, chatId);
            }

            return chat;
        } catch (Exception e) {
            log.error("Error removing user with ID {} from group chat with ID: {} by user with ID: {}", userId, chatId, reqUserId, e);
            throw new UserException("Error removing user from group chat" + e);
        }
    }

}