package co.teamsphere.teamsphere.controller;

import co.teamsphere.teamsphere.config.JWTTokenProvider;
import co.teamsphere.teamsphere.exception.UserException;
import co.teamsphere.teamsphere.models.User;
import co.teamsphere.teamsphere.repository.UserRepository;
import co.teamsphere.teamsphere.request.LoginRequest;
import co.teamsphere.teamsphere.response.AuthResponse;
import co.teamsphere.teamsphere.response.CloudflareApiResponse;
import co.teamsphere.teamsphere.services.CloudflareApiService;

import co.teamsphere.teamsphere.services.impl.CustomUserDetailsService;
import co.teamsphere.teamsphere.utils.GoogleAuthRequest;
import co.teamsphere.teamsphere.utils.GoogleUserInfo;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTTokenProvider jwtTokenProvider;

    private final CustomUserDetailsService customUserDetails;

    private final CloudflareApiService cloudflareApiService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JWTTokenProvider jwtTokenProvider,
                          CustomUserDetailsService customUserDetails,
                          CloudflareApiService cloudflareApiService
    ) {
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtTokenProvider=jwtTokenProvider;
        this.customUserDetails=customUserDetails;
        this.cloudflareApiService=cloudflareApiService;
    }

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

    @PostMapping(value="/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> userSignupMethod (@Valid @ModelAttribute User user, @RequestParam("file") MultipartFile file) throws UserException {
        try {
            log.info("Processing signup request for user with email: {}, username:{}",
                    user.getEmail(),user.getUsername());

            String email = user.getEmail();
            String password = user.getPassword();
            String username = user.getUsername();

            // Check if user with the given email or username already exists
            if (userRepository.findByEmail(email).isPresent()) {
                log.warn("Email={} is already used with another account", email);
                throw new UserException("Email is already used with another account");
            }

            if (userRepository.findByUsername(username).isPresent()) {
                log.warn("Username={} is already used with another account", username);
                throw new UserException("Username is already used with another account");
            }

            // Upload profile picture to Cloudflare
            CloudflareApiResponse responseEntity = cloudflareApiService.uploadImage(file, user);
            String baseUrl = Objects.requireNonNull(responseEntity.getResult().getVariants().get(0));
            String profileUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1) + "chatProfilePicture";


            // Creating a new user
            User createdUser = User.builder()
                    .email(email)
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .profilePicture(profileUrl)
                    .build();

            userRepository.save(createdUser);

            // auto-login after signup
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateJwtToken(authentication);

            AuthResponse authResponse = new AuthResponse(token, true);

            log.info("Signup process completed successfully for user with email: {}", email);

            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);

        } catch (UserException e) {
            log.error("Error during signup process", e);
            throw e; // Rethrow specific exception to be handled by global exception handler
        } catch (Exception e) {
            log.error("Unexpected error during signup process", e);
            throw new UserException("Unexpected error during signup process");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> userLoginMethod(@Valid @RequestBody LoginRequest loginRequest) throws UserException {
        try {
            log.info("Processing login request for user with username: {}", loginRequest.getEmail());

            String username = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            Authentication authentication = authentication(username, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateJwtToken(authentication);
            AuthResponse authResponse = new AuthResponse(token, true);

            log.info("Login successful for user with username: {}", username);

            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user with username: {}", loginRequest.getEmail());
            throw new UserException("Invalid username or password.");
        } catch (Exception e) {
            log.error("Unexpected error during login process", e);
            throw new UserException("Unexpected error during login process.");
        }
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> authenticateWithGoogle(@RequestBody GoogleAuthRequest request) {
        try {
            log.info("Processing Google authentication request");

            GoogleUserInfo googleUserInfo = request.getGoogleUserInfo();

            String email = googleUserInfo.getEmail();
            String username = googleUserInfo.getGiven_name();
            String pictureUrl = googleUserInfo.getPicture();

            // Check if user exists
            Optional<User> optionalUser = userRepository.findByEmail(email);
            User user;
            if (optionalUser.isPresent()) {
                user = optionalUser.get(); // User found, retrieve the existing user
                log.info("Existing user found with userId: {}", user.getId());
            } else {
                // Register a new user if not exists
                user = User.builder()
                        .email(email)
                        .username(username)
                        .profilePicture(pictureUrl)
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
