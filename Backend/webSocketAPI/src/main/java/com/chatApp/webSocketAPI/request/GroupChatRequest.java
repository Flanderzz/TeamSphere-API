package com.chatApp.webSocketAPI.request;

import java.util.List;

public class GroupChatRequest {
    private List<Integer> userIDs;
    private String chatName;
    private String chatImage;

    public List<Integer> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(List<Integer> userIDs) {
        this.userIDs = userIDs;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatImage() {
        return chatImage;
    }

    public void setChatImage(String chatImage) {
        this.chatImage = chatImage;
    }
}
