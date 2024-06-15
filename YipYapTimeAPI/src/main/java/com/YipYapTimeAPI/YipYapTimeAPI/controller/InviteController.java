package com.YipYapTimeAPI.YipYapTimeAPI.controller;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.InviteException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Invites;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.InviteRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.response.ApiResponse;
import com.YipYapTimeAPI.YipYapTimeAPI.services.EmailService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.InviteService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invite")
@Slf4j
public class InviteController {
    private final InviteRepository inviteRepository;
    UserService userService;
    EmailService emailService;
    InviteService inviteService;

    public InviteController(UserService userService, EmailService emailService, InviteService inviteService,
                            InviteRepository inviteRepository) {
        this.userService = userService;
        this.emailService = emailService;
        this.inviteService = inviteService;
        this.inviteRepository = inviteRepository;
    }

    //this is for the copy link and when user gets email
    // if not logged in, make them login then reroute them to this endpoint
    @GetMapping("/{ID}")
    public ResponseEntity<ApiResponse> inviteSetupHandler(@PathVariable String ID, @RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfile(jwt);


        return null;
    }

    //this is for the invite button
    @PostMapping("/{ID}")
    public ResponseEntity<ApiResponse> inviteHandler(@PathVariable String ID, @RequestHeader("Authorization") String jwt, @RequestBody List<String> emails) throws Exception {
        try {
            User inviter = userService.findUserProfile(jwt);

            String addressLink = inviteService.generateInvite(inviter, ID, emails);

            emails.forEach(email -> emailService.sendInviteEmail(email, addressLink, inviter));

            ApiResponse res = new ApiResponse("Invites Sent to Users!", true);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @GetMapping("/getID")
    public ResponseEntity<ApiResponse> getID(@RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfile(jwt);
        String invites = inviteService.getInviteID(user);
        ApiResponse res = new ApiResponse("Invites Found!", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/stopInvites")
    public ResponseEntity<ApiResponse> stopInvites(@RequestHeader("Authorization") String jwt) throws InviteException {
        User user = userService.findUserProfile(jwt);
        inviteService.pauseInvites(user);
        ApiResponse res = new ApiResponse("Invites Stopped!", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
