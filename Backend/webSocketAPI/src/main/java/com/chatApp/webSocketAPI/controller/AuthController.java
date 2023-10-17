package com.chatApp.webSocketAPI.controller;


import com.chatApp.webSocketAPI.Config.TokenProvider;
import com.chatApp.webSocketAPI.Exception.UserException;
import com.chatApp.webSocketAPI.model.User;
import com.chatApp.webSocketAPI.repository.UserRepository;
import com.chatApp.webSocketAPI.request.LoginRequest;
import com.chatApp.webSocketAPI.response.AuthResponse;
import com.chatApp.webSocketAPI.service.Impl.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private TokenProvider tokenProvider;
    private CustomUserService customUserService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider, CustomUserService customUserService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.customUserService = customUserService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException {
        String email = user.getEmail();
        String name = user.getName();
        String password = user.getPassword();

        User isUser = userRepository.findByEmail(email);

        if(isUser != null){
            throw new UserException("Email: " + email + " Already in use");
        }

        User isCreated = new User();
        isCreated.setEmail(email);
        isCreated.setName(name);
        isCreated.setPassword(passwordEncoder.encode(password));

        userRepository.save(isCreated);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse(jwt, true);

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest req){
        String email = req.getEmail();
        String password = req.getPassword();

        Authentication authentication = authenticating(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse(jwt, true);

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.ACCEPTED);

    }

    public Authentication authenticating(String username, String password){
        UserDetails userDetails = customUserService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid Username, Try again");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid Username or Password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
