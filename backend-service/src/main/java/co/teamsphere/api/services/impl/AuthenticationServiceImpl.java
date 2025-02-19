package co.teamsphere.api.services.impl;

import co.teamsphere.api.config.JWTTokenProvider;
import co.teamsphere.api.exception.CloudflareException;
import co.teamsphere.api.exception.ProfileImageException;
import co.teamsphere.api.exception.UserException;
import co.teamsphere.api.models.User;
import co.teamsphere.api.repository.UserRepository;
import co.teamsphere.api.request.SignupRequest;
import co.teamsphere.api.response.AuthResponse;
import co.teamsphere.api.response.CloudflareApiResponse;
import co.teamsphere.api.services.AuthenticationService;
import co.teamsphere.api.services.CloudflareApiService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetails;
    private final CloudflareApiService cloudflareApiService;

    public AuthenticationServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JWTTokenProvider jwtTokenProvider,
            CustomUserDetailsService customUserDetails,
            CloudflareApiService cloudflareApiService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetails = customUserDetails;
        this.cloudflareApiService = cloudflareApiService;
    }


    @Override
    @Transactional
    public AuthResponse signupUser(@Valid SignupRequest request) throws UserException, ProfileImageException, IOException {
        if (isEmailInvalid(request.getEmail())) {
            log.warn("Bad Email={} was passed in", request.getEmail());
            throw new UserException("Valid email was not passed in");
        }

        // Check if user with the given email or username already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Email={} is already used with another account", request.getEmail());
            throw new UserException("Email is already used with another account");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Username={} is already used with another account", request.getUsername());
            throw new UserException("Username is already used with another account");
        }

        if (request.getFile().isEmpty() || (!Objects.equals(request.getFile().getContentType(), "image/jpeg") && !Objects.equals(request.getFile().getContentType(), "image/png"))) {
            log.warn("File type not accepted, {}", request.getFile().getContentType());
            throw new ProfileImageException("Profile Picture type is not allowed!");
        }

        // Upload profile picture to Cloudflare
        CloudflareApiResponse responseEntity = cloudflareApiService.uploadImage(request.getFile());

        // Check if the Cloudflare API call was unsuccessful
        if (!responseEntity.isSuccess() || (responseEntity.getErrors() != null && !responseEntity.getErrors().isEmpty())) {
            String errorMessage = responseEntity.getErrors().stream()
                    .map(CloudflareException::getMessage)
                    .collect(Collectors.joining(", "));
            throw new ProfileImageException("Cloudflare upload failed: " + errorMessage);
        }


        String baseUrl = Objects.requireNonNull(responseEntity.getResult().getVariants().get(0));
        String profileUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1) + "public";

        var currentDateTime = LocalDateTime.now().atOffset(ZoneOffset.UTC);

        // Creating a new user
        var newUser = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePicture(profileUrl)
                .createdDate(currentDateTime)
                .lastUpdatedDate(currentDateTime)
                .build();

        userRepository.save(newUser);

        // auto-login after signup
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateJwtToken(authentication);

        return new AuthResponse(token, true);
    }

    @Override
    @Transactional
    public AuthResponse loginUser(String email, String password) throws UserException {
        if(isEmailInvalid(email)){
            log.warn("Email={} is already used with another account", email);
            throw new UserException("Email is already used with another account");
        }

        Authentication authentication = authentication(email, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateJwtToken(authentication);

        return new AuthResponse(token, true);
    }

    public static boolean isEmailInvalid(String email) {
        if (email.isEmpty())
            return true;

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        return !Pattern.compile(emailRegex).matcher(email).matches();
    }

    private Authentication authentication(String email, String password) {
        log.info("Authenticating user with email: {}", email);

        UserDetails userDetails = customUserDetails.loadUserByUsername(email);

        if (userDetails == null) {
            log.warn("User with email {} not found", email);
            throw new BadCredentialsException("Email or Password is invalid: " + email);
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.warn("Invalid password for user with email: {}", email);
            throw new BadCredentialsException("Email or Password is invalid: " + email);
        }

        log.info("Authentication successful for user with email: {}", email);

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
}
