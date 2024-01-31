package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {

    private Integer id;

    private String username;

    private String email;

    private String profile_picture;
}
