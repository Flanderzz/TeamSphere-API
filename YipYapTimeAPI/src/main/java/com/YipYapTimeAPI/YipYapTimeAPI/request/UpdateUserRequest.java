package com.YipYapTimeAPI.YipYapTimeAPI.request;

public class UpdateUserRequest {


    private String username;
    private String profile_picture;

    public UpdateUserRequest() {
        // TODO Auto-generated constructor stub
    }

    public UpdateUserRequest(String username, String profile_picture) {
        super();
        this.username = username;
        this.profile_picture = profile_picture;
    }

    public String getFull_name() {
        return username;
    }

    public void setFull_name(String full_name) {
        this.username = username;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }


}
