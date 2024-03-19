package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import lombok.*;

import java.util.UUID;


@Data
@Builder
public class UserDTO {
    private UUID id;

    private String username;

    private String email;

    private String phone;

    private String profile_picture;
}
