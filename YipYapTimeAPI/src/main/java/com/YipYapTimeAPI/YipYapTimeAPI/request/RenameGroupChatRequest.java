package com.YipYapTimeAPI.YipYapTimeAPI.request;

public class RenameGroupChatRequest {

    private String groupName;

    public RenameGroupChatRequest() {
        // TODO Auto-generated constructor stub
    }

    public RenameGroupChatRequest(String groupName) {
        super();
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}


