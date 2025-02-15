package co.teamsphere.api.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String username;

    @NotNull
    private MultipartFile file;
}
