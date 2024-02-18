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

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private UserService userService;

    private ChatRepository chatRepo;

    public ChatServiceImpl(UserService userService, ChatRepository chatRepo) {
        this.userService = userService;
        this.chatRepo = chatRepo;
    }

    @Override
    public Chat createChat(Integer reqUserId, Integer userId2, boolean isGroup) throws UserException {
        try {
            log.info("Creating chat. reqUserId: {}, userId2: {}, isGroup: {}", reqUserId, userId2, isGroup);

            User reqUser = userService.findUserById(reqUserId);
            User user2 = userService.findUserById(userId2);

            Chat isChatExist = chatRepo.findSingleChatByUsersId(user2, reqUser);

            if (isChatExist != null) {
                log.info("Chat already exists for users: {} and {}", reqUserId, userId2);
                return isChatExist;
            }

            Chat chat = new Chat();

            chat.setCreated_by(reqUser);
            chat.getUsers().add(reqUser);
            chat.getUsers().add(user2);
            chat.setIs_group(isGroup);

            Chat createdChat = chatRepo.save(chat);

            log.info("Chat created successfully. Chat ID: {}", createdChat.getId());

            return createdChat;
        } catch (Exception e) {
            log.error("Error creating chat", e);
            throw new UserException("Error creating chat" + e);
        }
    }

    @Override
    public Chat findChatById(Integer chatId) throws ChatException {
        try {
            log.info("Attempting to find chat by ID: {}", chatId);

            Optional<Chat> chat = chatRepo.findById(chatId);

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
    public List<Chat> findAllChatByUserId(Integer userId) throws UserException {
        try {
            log.info("Finding all chats for user with ID: {}", userId);

            User user = userService.findUserById(userId);

            List<Chat> chats = chatRepo.findChatByUserId(user.getId());

            log.info("Found {} chats for user with ID: {}", chats.size(), userId);

            return chats;
        } catch (Exception e) {
            log.error("Error finding chats for user with ID: {}", userId, e);
            throw new UserException("Error finding chats for user with ID: " + userId + e);
        }
    }

    @Override
    public Chat deleteChat(Integer chatId, Integer userId) throws ChatException, UserException {
        try {
            log.info("Deleting chat with ID: {} by user with ID: {}", chatId, userId);

            User user = userService.findUserById(userId);
            Chat chat = findChatById(chatId);

            if ((chat.getCreated_by().getId().equals(user.getId())) && !chat.getIs_group()) {
                chatRepo.deleteById(chat.getId());

                log.info("Chat deleted successfully. ID: {}, User ID: {}", chatId, userId);

                return chat;
            }

            log.info("User with ID {} doesn't have access to delete chat with ID: {}", userId, chatId);
            throw new ChatException("You don't have access to delete this chat");
        } catch (Exception e) {
            log.error("Error deleting chat with ID: {} by user with ID: {}", chatId, userId, e);
            throw new ChatException("Error deleting chat with ID: " + chatId + e);
        }
    }

    @Override
    public Chat createGroup(GroupChatRequest req, Integer reqUserId) throws UserException {
        try {
            log.info("Creating group chat. Requested by user with ID: {}", reqUserId);

            User reqUser = userService.findUserById(reqUserId);

            Chat chat = new Chat();

            chat.setCreated_by(reqUser);
            chat.getUsers().add(reqUser);

            for (Integer userId : req.getUserIds()) {
                User user = userService.findUserById(userId);
                if (user != null) {
                    chat.getUsers().add(user);
                    log.info("Added user with ID {} to the group chat", userId);
                } else {
                    log.warn("User with ID {} not found while creating the group chat", userId);
                }
            }

            chat.setChat_name(req.getChat_name());
            chat.setChat_image(req.getChat_image());
            chat.setIs_group(true);
            chat.getAdmins().add(reqUser);

            Chat createdChat = chatRepo.save(chat);

            log.info("Group chat created successfully. Chat ID: {}", createdChat.getId());

            return createdChat;
        } catch (Exception e) {
            log.error("Error creating group chat", e);
            throw new UserException("Error creating group chat" + e);
        }
    }


    @Override
    public Chat addUserToGroup(Integer userId, Integer chatId) throws UserException, ChatException {
        try {
            log.info("Adding user with ID {} to group chat with ID: {}", userId, chatId);

            Chat chat = findChatById(chatId);
            User user = userService.findUserById(userId);

            chat.getUsers().add(user);

            Chat updatedChat = chatRepo.save(chat);

            log.info("User with ID {} added to group chat successfully. Updated chat ID: {}", userId, chatId);

            return updatedChat;
        } catch (Exception e) {
            log.error("Error adding user with ID {} to group chat with ID: {}", userId, chatId, e);
            throw new UserException("Error adding user to group chat" + e);
        }
    }

    @Override
    public Chat renameGroup(Integer chatId, String groupName, Integer reqUserId) throws ChatException, UserException {
        try {
            log.info("Renaming group chat with ID: {} to: {} by user with ID: {}", chatId, groupName, reqUserId);

            Chat chat = findChatById(chatId);
            User user = userService.findUserById(reqUserId);

            if (chat.getUsers().contains(user)) {
                chat.setChat_name(groupName);
                log.info("Group chat renamed successfully to: {}", groupName);
            } else {
                log.warn("User with ID {} doesn't have permission to rename group chat with ID: {}", reqUserId, chatId);
            }

            return chatRepo.save(chat);
        } catch (Exception e) {
            log.error("Error renaming group chat with ID: {} by user with ID: {}", chatId, reqUserId, e);
            throw new UserException("Error renaming group chat" + e);
        }
    }

    @Override
    public Chat removeFromGroup(Integer chatId, Integer userId, Integer reqUserId) throws UserException, ChatException {
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