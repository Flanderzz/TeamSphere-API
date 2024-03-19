package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDTO {
    private String content;

    private Long id;

    private LocalDateTime timeStamp;

    private Boolean is_read;

    private UserDTO user;

    private ChatDTO chat;
}
