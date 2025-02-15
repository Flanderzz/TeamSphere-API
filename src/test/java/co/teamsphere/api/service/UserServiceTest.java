package co.teamsphere.api.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import co.teamsphere.api.config.JWTTokenProvider;
import co.teamsphere.api.exception.ProfileImageException;
import co.teamsphere.api.exception.UserException;
import co.teamsphere.api.models.User;
import co.teamsphere.api.repository.UserRepository;
import co.teamsphere.api.request.UpdateUserRequest;
import co.teamsphere.api.response.CloudflareApiResponse;
import co.teamsphere.api.services.CloudflareApiService;
import co.teamsphere.api.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private JWTTokenProvider jwtTokenProvider;

    @Mock
    private CloudflareApiService cloudflareApiService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setProfilePicture("https://example.com/profile.jpg");
        user.setLastUpdatedDate(LocalDateTime.now().atOffset(ZoneOffset.UTC));



    }

    @Test
    void saveUser_ReturnsSavedUser() {
        when(userRepo.save(any(User.class))).thenReturn(user);

        User savedUser = userRepo.save(user);

        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
    }



    @Test
    void findUserById_ThrowsException_WhenUserDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.findUserById(nonExistentId));
    }


    @Test
    void searchUser_ReturnsListOfUsers_WhenMatchesFound() {
        List<User> users = List.of(user);

        when(userRepo.searchUsers("testUser")).thenReturn(users);

        List<User> results = userService.searchUser("testUser");

        verify(userRepo, times(1)).searchUsers("testUser");
        assertEquals(1, results.size());
        assertEquals("testUser", results.get(0).getUsername());
    }


    @Test
    void searchUser_ReturnsEmptyList_WhenNoMatchesFound() {
        when(userRepo.searchUsers("nonexistent")).thenReturn(Collections.emptyList());

        List<User> results = userService.searchUser("nonexistent");

        verify(userRepo, times(1)).searchUsers("nonexistent");
        assertTrue(results.isEmpty());
    }


    @Test
    void findUserProfile_ReturnsUser_WhenValidJWT() {
        when(userRepo.save(any(User.class))).thenReturn(user);
        User savedUser = userRepo.save(user);

        String mockJwt = "mock.jwt.token";
        when(jwtTokenProvider.getEmailFromToken(mockJwt)).thenReturn(savedUser.getEmail());
        when(userRepo.findByEmail(savedUser.getEmail())).thenReturn(Optional.of(savedUser));
        User profile = userService.findUserProfile(mockJwt);


        assertNotNull(profile);
        assertEquals(savedUser.getEmail(), profile.getEmail());
    }



    @Test
    void findUserProfile_ThrowsException_WhenInvalidJWT() {
        String mockJwt = "invalid.jwt.token";
        when(jwtTokenProvider.getEmailFromToken(mockJwt)).thenReturn("wrong@example.com");
        when(userRepo.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> userService.findUserProfile(mockJwt));
    }


    @Test
    void updateUser_UpdatesUser_WhenValidRequest() throws UserException, ProfileImageException {
        when(userRepo.save(any(User.class))).thenReturn(user);
        User savedUser = userRepo.save(user);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("updatedUser");

        when(userRepo.findById(savedUser.getId())).thenReturn(Optional.of(savedUser));

        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        User updatedUser = userService.updateUser(savedUser.getId(), request);

        assertNotNull(updatedUser);
        assertEquals("updatedUser", updatedUser.getUsername());
    }



    @Test
    void updateUser_ThrowsException_WhenUserNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest();

        when(userRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.updateUser(nonExistentId, request));
    }


//    @Test
//    void updateUser_UpdatesProfilePicture_WhenValidRequest() throws UserException, ProfileImageException, IOException {
//        UpdateUserRequest request = new UpdateUserRequest();
//        request.setProfile_picture(mockMultipartFile("image.png", "image/png"));
//
//        CloudflareApiResponse mockDeleteResponse = new CloudflareApiResponse();
//        mockDeleteResponse.setSuccess(true);
//
//        CloudflareApiResponse mockUploadResponse = new CloudflareApiResponse();
//        mockUploadResponse.setSuccess(true);
//        mockUploadResponse.setResult(new CloudflareApiResponse.Result(List.of("https://newurl.com/public")));
//
//        when(cloudflareApiService.deleteImage(anyString())).thenReturn(mockDeleteResponse);
//        when(cloudflareApiService.uploadImage(any())).thenReturn(mockUploadResponse);
//        when(userRepo.save(any(User.class))).thenReturn(user);
//
//        User updatedUser = userService.updateUser(user.getId(), request);
//
//        assertNotNull(updatedUser);
//        assertEquals("https://newurl.com/public", updatedUser.getProfilePicture());
//    }


    @Test
    void updateUser_ThrowsException_WhenProfileUploadFails() throws IOException {
        when(userRepo.save(any(User.class))).thenReturn(user);
        User savedUser = userRepo.save(user);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setProfile_picture(mockMultipartFile("image.png", "image/png"));

        CloudflareApiResponse mockDeleteResponse = new CloudflareApiResponse();
        mockDeleteResponse.setSuccess(true);

        CloudflareApiResponse mockUploadResponse = new CloudflareApiResponse();
        mockUploadResponse.setSuccess(false);

        when(userRepo.findById(savedUser.getId())).thenReturn(Optional.of(savedUser));



        assertThrows(UserException.class, () -> userService.updateUser(savedUser.getId(), request));
    }



    private org.springframework.web.multipart.MultipartFile mockMultipartFile(String filename, String contentType) {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);

        return file;
    }
}



