package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class MessageDTO {
    private String content;

    private Integer id;

    private LocalDateTime timeStamp;

    private Boolean is_read;

    private UserDTO user;

    private ChatDTO chat;
}
