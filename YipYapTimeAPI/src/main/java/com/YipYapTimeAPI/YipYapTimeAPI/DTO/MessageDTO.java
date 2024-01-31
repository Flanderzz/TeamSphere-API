package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageDTO {
    private String content;

    private Integer id;

    private LocalDateTime timeStamp;

    private Boolean is_read;

    private UserDTO user;

    private ChatDTO chat;
}
