package com.YipYapTimeAPI.YipYapTimeAPI.services.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.YipYapTimeAPI.YipYapTimeAPI.config.JWTTokenProvider;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.ProfileImageException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.UserRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.request.SignupRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.response.AuthResponse;
import com.YipYapTimeAPI.YipYapTimeAPI.response.CloudflareApiResponse;
import com.YipYapTimeAPI.YipYapTimeAPI.services.AuthenticationService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.CloudflareApiService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
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
    public AuthResponse signupUser(@Valid SignupRequest request) throws UserException, ProfileImageException {
        String email = request.getEmail();
        String password = request.getPassword();
        String username = request.getUsername();
        MultipartFile imageFile = request.getFile();
        try {
            if (isEmailInvalid(email)) {
                log.warn("Bad Email={} was passed in", email);
                throw new UserException("Valid email was not passed in");
            }

            // Check if user with the given email or username already exists
            if (userRepository.findByEmail(email).isPresent()) {
                log.warn("Email={} is already used with another account", email);
                throw new UserException("Email is already used with another account");
            }

            if (userRepository.findByUsername(username).isPresent()) {
                log.warn("Username={} is already used with another account", username);
                throw new UserException("Username is already used with another account");
            }

            if (imageFile.isEmpty() || (!imageFile.getContentType().equals("image/jpeg") && !imageFile.getContentType().equals("image/png"))){
                log.warn("File type not accepted, {}", imageFile.getContentType());
                throw new ProfileImageException("Profile Picture type is not allowed!");
            }

            // Upload profile picture to Cloudflare
            CloudflareApiResponse responseEntity = cloudflareApiService.uploadImage(imageFile);
            if (!responseEntity.isSuccess() || responseEntity == null || responseEntity.getResult() == null || responseEntity.getResult().getVariants() == null || responseEntity.getResult().getVariants().isEmpty()) {
                log.error("Error uploading profile picture to Cloudflare");
                throw new ProfileImageException("Error uploading profile picture to Cloudflare");
            }
            String baseUrl = Objects.requireNonNull(responseEntity.getResult().getVariants().get(0));
            String profileUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1) + "chatProfilePicture";

            var currentDateTime = LocalDateTime.now().atOffset(ZoneOffset.UTC);

            // Creating a new user
            var newUser = User.builder()
                    .email(email)
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .profilePicture(profileUrl)
                    .createdDate(currentDateTime)
                    .lastUpdatedDate(currentDateTime)
                    .build();

            userRepository.save(newUser);

            // auto-login after signup
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateJwtToken(authentication);

            return new AuthResponse(token, true);
        }
        catch (UserException e) {
            // TODO: think about returning a response and not throwing an error in a catch block
            log.error("Error during signup process", e);
            throw e; // Rethrow specific exception to be handled by global exception handler
        }
        catch (ProfileImageException e){
            log.error("ERROR: {}", e.getMessage());
            throw new ProfileImageException(e.getMessage());
        }
        catch (Exception e) {
            log.error("Unexpected error during signup process", e);
            throw new UserException("Unexpected error during signup process");
        }
    }

    @Override
    @Transactional
    public AuthResponse loginUser(String email, String password) throws UserException {
        try {
            if(isEmailInvalid(email)){
                log.warn("Email={} is already used with another account", email);
                throw new UserException("Email is already used with another account");
            }

            Authentication authentication = authentication(email, password);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateJwtToken(authentication);

            return new AuthResponse(token, true);
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user with username: {}", email);
            throw new UserException("Invalid username or password.");
        } catch (Exception e) {
            log.error("Unexpected error during login process", e);
            throw new UserException("Unexpected error during login process.");
        }
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
