package com.YipYapTimeAPI.YipYapTimeAPI.controller;

import com.YipYapTimeAPI.YipYapTimeAPI.config.JWTTokenProvider;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.UserRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.request.LoginRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.response.AuthResponse;
import com.YipYapTimeAPI.YipYapTimeAPI.services.impl.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JWTTokenProvider jwtTokenProvider;

    private CustomUserDetailsService customUserDetails;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetails) {
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtTokenProvider=jwtTokenProvider;
        this.customUserDetails=customUserDetails;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> userSignupMethod (@Valid @RequestBody User user) throws UserException {

        String email = user.getEmail();
        String password = user.getPassword();
        String username = user.getUsername();

        Optional<User> isEmailExist = userRepository.findByEmail(email);

        // Check if user with the given email already exists
        if (isEmailExist.isPresent())
            throw new UserException("Email Is Already Used With Another Account");


        // Creating a new user
        // TODO: builder pattern pls
        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setUsername(username);
        createdUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(createdUser);

        // Authenticate user and generate JWT token
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateJwtToken(authentication);

        AuthResponse authResponse = new AuthResponse(token, true);

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> userLoginMethod (@Valid @RequestBody LoginRequest loginRequest) throws UserException {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authentication(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateJwtToken(authentication);
        AuthResponse authResponse = new AuthResponse(token, true);

        return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.OK);
    }

    private Authentication authentication(String email, String password) {
        UserDetails userDetails = customUserDetails.loadUserByUsername(email);

        if (userDetails == null)
            throw new BadCredentialsException("Email or Password is invalid: " + email);


        if (!passwordEncoder.matches(password, userDetails.getPassword()))
            throw new BadCredentialsException("Email or Password is invalid: " + email);


        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

}
