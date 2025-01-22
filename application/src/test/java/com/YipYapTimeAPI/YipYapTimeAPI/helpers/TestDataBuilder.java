package com.YipYapTimeAPI.YipYapTimeAPI.helpers;

import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Messages;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;

import java.util.Set;
import java.util.UUID;

public class TestDataBuilder {

    public static User buildUser(String username, String profilePicture, String password, String email) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setProfilePicture(profilePicture);
        user.setPassword(password);
        user.setEmail(email);
        return user;
    }
    public static Chat buildChat(String chatName, String chatImage, boolean isGroup, Set<User> users) {
        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.setChatName(chatName);
        chat.setChatImage(chatImage);
        chat.setIsGroup(isGroup);
        chat.setUsers(users);
        return chat;
    }

    public static Messages buildMessage(String content, Chat chat, User username) {
        Messages message = new Messages();
        message.setId(UUID.randomUUID());
        message.setContent(content);
        message.setChat(chat);
        message.setUsername(username);
        return message;
    }
}

