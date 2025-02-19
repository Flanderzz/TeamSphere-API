package co.teamsphere.api.services;

import co.teamsphere.api.exception.ProfileImageException;
import co.teamsphere.api.exception.UserException;
import co.teamsphere.api.request.SignupRequest;
import co.teamsphere.api.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface AuthenticationService {
    AuthResponse signupUser(@Valid SignupRequest request) throws UserException, ProfileImageException, IOException;

    AuthResponse loginUser(String username, String password) throws UserException;
}
