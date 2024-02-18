package com.YipYapTimeAPI.YipYapTimeAPI.DTO;

import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserDTO {

    private Integer id;

    private String username;

    private String email;

    private String profile_picture;
}
