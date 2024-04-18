package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import jakarta.persistence.Column;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChatDTO {
    private UUID id;
    private String chat_name;
    private String chat_image;

    private Boolean is_group;

    private Set<UserDTO> admins= new HashSet<>();

    private UserDTO created_by;

    private Set<UserDTO> users = new HashSet<>();

    private List<MessageDTO> messages=new ArrayList<>();
}
