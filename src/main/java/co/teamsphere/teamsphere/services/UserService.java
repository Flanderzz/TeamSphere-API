package co.teamsphere.teamsphere.services;

import co.teamsphere.teamsphere.exception.UserException;
import co.teamsphere.teamsphere.models.User;
import co.teamsphere.teamsphere.request.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService{

    User findUserProfile(String jwt);

    User updateUser(UUID userId, UpdateUserRequest req) throws UserException;

    User findUserById(UUID userId) throws UserException;

    List<User> searchUser(String query);
}
