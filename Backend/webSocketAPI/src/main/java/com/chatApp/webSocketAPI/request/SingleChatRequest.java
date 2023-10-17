package com.chatApp.webSocketAPI.request;

public class SingleChatRequest {
    private Integer userID;

    public SingleChatRequest(){}

    public SingleChatRequest(Integer userID) {
        this.userID = userID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}
