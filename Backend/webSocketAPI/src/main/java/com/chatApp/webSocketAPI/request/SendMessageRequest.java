package com.chatApp.webSocketAPI.request;

public class SendMessageRequest {

    private Integer userID;
    private Integer chatID;
    private String content;

    public SendMessageRequest() {}

    public SendMessageRequest(Integer userID, Integer chatID, String content) {
        this.userID = userID;
        this.chatID = chatID;
        this.content = content;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getChatID() {
        return chatID;
    }

    public void setChatID(Integer chatID) {
        this.chatID = chatID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
