package co.teamsphere.teamsphere.request;

//TODO: add lombok annotations

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateUserRequest {
    private String username;
    private String profile_picture;
}
