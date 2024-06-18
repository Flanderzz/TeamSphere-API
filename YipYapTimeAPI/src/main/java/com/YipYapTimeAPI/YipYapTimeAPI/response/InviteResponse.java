package com.YipYapTimeAPI.YipYapTimeAPI.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InviteResponse<T>{
    private T ReturnType;
    private boolean status;
}

