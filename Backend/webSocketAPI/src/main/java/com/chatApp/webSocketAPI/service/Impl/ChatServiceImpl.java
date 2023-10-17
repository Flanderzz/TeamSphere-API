package com.chatApp.webSocketAPI.service.Impl;

import com.chatApp.webSocketAPI.Exception.ChatException;
import com.chatApp.webSocketAPI.Exception.UserException;
import com.chatApp.webSocketAPI.model.Chat;
import com.chatApp.webSocketAPI.model.User;
import com.chatApp.webSocketAPI.repository.ChatRepository;
import com.chatApp.webSocketAPI.request.GroupChatRequest;
import com.chatApp.webSocketAPI.service.ChatService;
import com.chatApp.webSocketAPI.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {
    private ChatRepository chatRepository;
    private UserService userService;
    @Override
    public Chat createChat(User requestedUserID, Integer userID) throws UserException {
        User user = userService.findByID(userID);
        Chat doesChatExist = chatRepository.findSingleChatByUserID(user, requestedUserID);

        if (doesChatExist != null) {
            return doesChatExist;
        }

        Chat chat = new Chat();
        chat.setCreatedBy(requestedUserID);
        chat.getUsers().add(user);
        chat.getUsers().add(requestedUserID);
        chat.setGroupChat(false);

        return chat;
    }

    @Override
    public Chat findChatByID(Integer chatID) throws ChatException {
        Optional<Chat> chat = chatRepository.findById(chatID);

        if (!chat.isPresent()) {
            return chat.get();
        }
        throw new ChatException("No Chat Exist with ID: " + chat);
    }

    @Override
    public Chat addUserToGroupChat(Integer userID, Integer chatID, User requestedUser) throws UserException, ChatException {
        Optional<Chat> find = chatRepository.findById(chatID);
        User user = userService.findByID(userID);

        if (find.isPresent()) {
            Chat chat = find.get();
            if (chat.getAdmins().contains(requestedUser)){
                chat.getUsers().add(user);
                return chatRepository.save(chat);
            } else {
                throw new UserException("No Permission: You Are Not an admin!");
            }
        }
        throw new ChatException("Cannot Find Chat With ID: " +find);
    }

    @Override
    public Chat createGroupChat(GroupChatRequest request, User requestedUserID) throws UserException {
        Chat groupChat = new Chat();

        groupChat.setGroupChat(true);
        groupChat.setChatImage(request.getChatImage());
        groupChat.setChatName(request.getChatName());
        groupChat.setCreatedBy(requestedUserID);
        groupChat.getAdmins().add(requestedUserID);

        for (Integer userID:request.getUserIDs()){
            User user = userService.findByID(userID);
            groupChat.getUsers().add(user);
        }

        return groupChat;
    }

    @Override
    public Chat renameGroupChat(Integer chatID, String groupChatName, User requestedUserID) throws ChatException, UserException {
        Optional<Chat> option = chatRepository.findById(chatID);

        if (option.isPresent()) {
            Chat chat = option.get();
            if(chat.getUsers().contains(requestedUserID)){
                chat.setChatName(groupChatName);
                return chatRepository.save(chat);
            }
            throw new ChatException("You Are Not Apart Of This Group Chat!");
        }
        throw new ChatException("Cannot Find Chat With ID: " +option);
    }

    @Override
    public Chat removeUserFromGroupChat(Integer chatID, Integer userID, User reqUserID) throws ChatException, UserException {
        Optional<Chat> find = chatRepository.findById(chatID);
        User user = userService.findByID(userID);

        if (find.isPresent()) {
            Chat chat = find.get();
            if (chat.getAdmins().contains(reqUserID)){
                chat.getUsers().remove(user);
                return chatRepository.save(chat);
                //Find a better way to do this pls
            } else if (chat.getUsers().contains(reqUserID)){
                if (user.getID().equals(reqUserID.getID())){
                    chat.getUsers().remove(user);
                    return chatRepository.save(chat);
                }

            }
            throw new UserException("No Permission: You Cannot Remove Others!");

        }
        throw new ChatException("Cannot Find Chat With ID: " +find);
    }

    @Override
    public void deleteChat(Integer chatID, Integer userID) throws ChatException, UserException {
        Optional<Chat> finder = chatRepository.findById(chatID);

        if (finder.isPresent()){
            Chat chat = finder.get();
            chatRepository.deleteById(chat.getID());
        }
    }

    @Override
    public List<Chat> findAllChatByUserID(Integer userID) throws UserException {
        User user = userService.findByID(userID);

        List<Chat> chats = chatRepository.findChatByUserID(user.getID());
        return chats;
    }
}
