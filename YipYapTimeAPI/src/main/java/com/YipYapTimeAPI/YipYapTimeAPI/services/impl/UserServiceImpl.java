package com.YipYapTimeAPI.YipYapTimeAPI.services.impl;

import com.YipYapTimeAPI.YipYapTimeAPI.config.JWTTokenProvider;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.UserRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.request.UpdateUserRequest;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepo, JWTTokenProvider jwtTokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User updateUser(Integer userId, UpdateUserRequest req) throws UserException {

        System.out.println("finding user ");
        User user = findUserById(userId);

        System.out.println("update find user " + user);

        if (req.getFull_name() != null) {
            user.setUsername(req.getFull_name());
        }
        if (req.getProfile_picture() != null) {
            user.setProfile_image(req.getProfile_picture());
        }

        return userRepo.save(user);
    }

    @Override
    public User findUserById(Integer userId) throws UserException {

        Optional<User> opt = userRepo.findById(userId);

        if (opt.isPresent()) {
            User user = opt.get();

            return user;
        }
        throw new UserException("user doesnt exist with the id: " + userId);
    }

    @Override
    public User findUserProfile(String jwt) {
        String email = jwtTokenProvider.getEmailFromToken(jwt);

        Optional<User> opt = userRepo.findByEmail(email);

        if (opt.isPresent()) {
            return opt.get();
        }

        throw new BadCredentialsException("received invalid token!");
    }

    @Override
    public List<User> searchUser(String query) {
        return userRepo.searchUsers(query);

    }
}