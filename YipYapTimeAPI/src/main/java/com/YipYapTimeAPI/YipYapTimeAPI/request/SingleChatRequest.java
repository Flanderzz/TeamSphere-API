package com.YipYapTimeAPI.YipYapTimeAPI.request;

public class SingleChatRequest {



    private Integer userId;

    public SingleChatRequest() {
        // TODO Auto-generated constructor stub
    }

    public SingleChatRequest(Integer userId) {
        super();
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


}
