package com.YipYapTimeAPI.YipYapTimeAPI.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SendMessageRequest {

    private Integer chatId;
    private Integer userId;
    private String content;
}
