package com.YipYapTimeAPI.YipYapTimeAPI.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthResponse {

    private String jwt;

    private boolean status;
}
