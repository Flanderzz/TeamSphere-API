package co.teamsphere.api.controller;

import co.teamsphere.api.config.JWTTokenProvider;
import co.teamsphere.api.exception.ProfileImageException;
import co.teamsphere.api.exception.UserException;
import co.teamsphere.api.models.User;
import co.teamsphere.api.repository.UserRepository;
import co.teamsphere.api.request.LoginRequest;
import co.teamsphere.api.request.SignupRequest;
import co.teamsphere.api.response.AuthResponse;
import co.teamsphere.api.response.ErrorResponse;
import co.teamsphere.api.services.AuthenticationService;
import co.teamsphere.api.utils.GoogleAuthRequest;
import co.teamsphere.api.utils.GoogleUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTTokenProvider jwtTokenProvider;

    private final AuthenticationService authenticationService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JWTTokenProvider jwtTokenProvider,
                          AuthenticationService authenticationService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Verify JWT Token", description = "Check if a provided JWT token is valid.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token is invalid or not provided"
            )
    })
    @GetMapping("/verify")
    public ResponseEntity<String> verifyJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            // This block is unlikely to be hit as the filter would reject invalid tokens
            return new ResponseEntity<>("Token is invalid or not provided.", HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Sign up a new user", description = "Register new user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> userSignupMethod(
            @Schema(description = "User details", implementation = SignupRequest.class)
            @Valid @ModelAttribute SignupRequest request) {

        log.info("Processing signup request for user with email: {}, username: {}", request.getEmail(), request.getUsername());

        try {
            AuthResponse authResponse = authenticationService.signupUser(request);
            log.info("Signup process completed successfully for user with email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);

        } catch (UserException e) {
            log.error("User-related error during signup process", e);
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), "/signup");

        } catch (ProfileImageException e) {
            log.warn("File type not accepted: {}", request.getFile().getContentType());
            return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Profile picture type is not allowed!", "/signup");

        } catch (Exception e) {
            log.error("Unexpected error during signup process", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during signup process", "/signup");
        }
    }


    @Operation(summary = "Login a user", description = "Login with email and password.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> userLoginMethod(
            @Schema(description = "Login request body", implementation = LoginRequest.class)
            @Valid @RequestBody LoginRequest loginRequest) {

        log.info("Processing login request for user with username: {}", loginRequest.getEmail());

        try {
            AuthResponse authResponse = authenticationService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            log.info("Login successful for user with username: {}", loginRequest.getEmail());
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user with username: {}", loginRequest.getEmail());
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password.", "/login");

        } catch (Exception e) {
            log.error("Unexpected error during login process", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during login process.", "/login");
        }
    }

    //TODO: move business logic to service layer
    @Transactional
    @Operation(summary = "Authenticate via Google", description = "login/signup via Google OAuth.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Google authentication failed")
    })
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> authenticateWithGoogleMethod(
            @Schema(
                    description = "Google OAuth request body",
                    implementation = GoogleAuthRequest.class
            )
            @RequestPart("googleUser")
            @RequestBody GoogleAuthRequest request) {
        try {
            log.info("Processing Google authentication request");

            GoogleUserInfo googleUserInfo = request.getGoogleUserInfo();

            String email = googleUserInfo.getEmail();
            String username = googleUserInfo.getName();
            String pictureUrl = googleUserInfo.getPicture();

            // Check if user exists
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                log.info("Existing user found with userId: {}", optionalUser.get().getId());
            } else {
                // Register a new user if not exists
                var currentDateTime = LocalDateTime.now().atOffset(ZoneOffset.UTC);
                var user = User.builder()
                        .email(email)
                        .username(username)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString())) // consider adding this cause a userpass field should NEVER be null
                        .profilePicture(pictureUrl)
                        .createdDate(currentDateTime)
                        .lastUpdatedDate(currentDateTime)
                        .build();

                userRepository.save(user);
                log.info("New user created with email: {}", email);
            }

            // Load UserDetails and set authentication context
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = jwtTokenProvider.generateJwtToken(authentication);

            AuthResponse authResponse = new AuthResponse(token, true);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error during Google authentication: ", e);
            return new ResponseEntity<>(new AuthResponse("Error during Google authentication: " + e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String endpoint) {
        ErrorResponse errorResponse = new ErrorResponse(
                new ErrorResponse.ErrorDetails(
                        status.value(),
                        message,
                        "An error occurred while processing the request.",
                        endpoint,
                        "POST",
                        Instant.now().toString(),
                        UUID.randomUUID().toString() // Unique request ID for tracking
                )
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}
