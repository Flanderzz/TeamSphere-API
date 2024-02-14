package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import jakarta.persistence.Column;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatDTO {
    private Integer id;
    private String chat_name;
    private String chat_image;

    private Boolean is_group;

    private Set<UserDTO> admins= new HashSet<>();

    private UserDTO created_by;

    private Set<UserDTO> users = new HashSet<>();

    private List<MessageDTO> messages=new ArrayList<>();
}
