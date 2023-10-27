package com.chatApp.webSocketAPI.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private boolean isAuthenticated;

    public AuthResponse(String jwt, boolean isAuthenticated) {
        super();
        this.jwt = jwt;
        this.isAuthenticated = isAuthenticated;
    }


}
