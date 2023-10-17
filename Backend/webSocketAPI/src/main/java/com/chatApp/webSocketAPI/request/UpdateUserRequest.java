package com.chatApp.webSocketAPI.request;

public class UpdateUserRequest {

    private String fullName;
    private String profilePic;

    public UpdateUserRequest(String fullName, String profilePic) {
        this.fullName = fullName;
        this.profilePic = profilePic;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
