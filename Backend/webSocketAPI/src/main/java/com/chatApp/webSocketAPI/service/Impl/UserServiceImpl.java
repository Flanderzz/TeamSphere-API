package com.chatApp.webSocketAPI.service.Impl;

import com.chatApp.webSocketAPI.Config.TokenProvider;
import com.chatApp.webSocketAPI.Exception.UserException;
import com.chatApp.webSocketAPI.model.User;
import com.chatApp.webSocketAPI.repository.UserRepository;
import com.chatApp.webSocketAPI.request.UpdateUserRequest;
import com.chatApp.webSocketAPI.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    TokenProvider tokenProvider;

    public UserServiceImpl (UserRepository userRepository, TokenProvider tokenProvider){
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public User findByID(Integer ID) throws UserException {
        Optional<User> opt = userRepository.findById(ID);

        if (opt.isPresent()){
            return opt.get();
        }
        throw new UserException("No User By This ID: " + ID);
    }

    @Override
    public User findUserProfile(String jwt) throws UserException {
        String email = tokenProvider.getEmailFromToken(jwt);

        if(email == null){ throw new BadCredentialsException("User Profile Not Found: Invalid Token"); }

        User user = userRepository.findByEmail(email);

        if(user == null) { throw new UserException("User Not Found With Email: "+email); }

        return user;

    }

    @Override
    public List<User> searchUsers(String query) {
        List<User> users = userRepository.searchUser(query);

        return users;
    }

    @Override
    public User updateUser(Integer ID, UpdateUserRequest request) throws UserException {
        User user = findByID(ID);

        // Can be an issue, relook over how we did the User model and fix it
        if(request.getFullName() != null){ user.setName(request.getFullName()); }
        if(request.getProfilePic() != null){ user.setProfile_pic(request.getProfilePic()); }

        return userRepository.save(user);
    }
}
