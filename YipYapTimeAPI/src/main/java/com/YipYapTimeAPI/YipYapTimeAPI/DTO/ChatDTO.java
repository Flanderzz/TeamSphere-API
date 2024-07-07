package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class ChatDTO {
    private UUID id;
    private String chatName;
    private String chatImage;
    private Boolean isGroup;
    private Set<UUID> admins = new HashSet<>();
    private UUID createdBy;
    private Set<UUID> users = new HashSet<>();
    private List<UUID> messages = new ArrayList<>();
}
