package com.YipYapTimeAPI.YipYapTimeAPI.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupChatRequest {

    private List<UUID> userIds;

    private String chat_name;

    private String chat_image;
}
