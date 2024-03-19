package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import jakarta.persistence.Column;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class ChatDTO {
    private Integer id;
    private String chat_name;
    private String chat_image;

    private Boolean is_group;

    private Set<UserDTO> admins;

    private UserDTO created_by;

    private Set<UserDTO> users;

    private List<MessageDTO> messages;
}
