package com.YipYapTimeAPI.YipYapTimeAPI.controller;

import com.YipYapTimeAPI.YipYapTimeAPI.config.JWTTokenProvider;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.UserRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.request.LoginRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.response.AuthResponse;
import com.YipYapTimeAPI.YipYapTimeAPI.response.CloudflareApiResponse;
import com.YipYapTimeAPI.YipYapTimeAPI.services.CloudflareApiService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.impl.CustomUserDetailsService;
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

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JWTTokenProvider jwtTokenProvider;

    private CustomUserDetailsService customUserDetails;

    private CloudflareApiService cloudflareApiService;

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

    @PostMapping(value="/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> userSignupMethod (@Valid @ModelAttribute User user, @RequestParam("file") MultipartFile file) throws UserException {
        try {
            log.info("Processing signup request for user with email: {}, username:{}",
                    user.getEmail(),user.getUsername());

            String email = user.getEmail();
            String password = user.getPassword();
            String username = user.getUsername();

            Optional<User> isEmailExist = userRepository.findByEmail(email);

            // Check if user with the given email already exists
            if (isEmailExist.isPresent()) {
                log.warn("Email {} is already used with another account", email);
                throw new UserException("Email is already used with another account");
            }

            log.info("User with email {} successfully created", email);

            // Authenticate user and generate JWT token
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateJwtToken(authentication);

            AuthResponse authResponse = new AuthResponse(token, true);

            CloudflareApiResponse responseEntity = cloudflareApiService.uploadImage(file, user);

            String baseUrl = Objects.requireNonNull(responseEntity.getResult().getVariants().get(0));
            String profile_url = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1) + "chatProfilePicture";


            // Creating a new user
            User createdUser = User.builder()
                    .email(email)
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .profile_image(profile_url)
                    .build();

            userRepository.save(createdUser);

            log.info("Signup process completed successfully for user with email: {}", email);

            return new ResponseEntity<>(authResponse, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error during signup process", e);
            throw new UserException("Error during signup process" + e);
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
        } catch (Exception e) {
            log.error("Error during login process", e);
            throw new UserException("Error during login process" + e);
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
