package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

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
