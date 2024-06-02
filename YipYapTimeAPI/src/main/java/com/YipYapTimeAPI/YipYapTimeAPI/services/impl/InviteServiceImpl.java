package com.YipYapTimeAPI.YipYapTimeAPI.services.impl;

import com.YipYapTimeAPI.YipYapTimeAPI.enums.Status;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.InviteException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Invites;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.models.embeddables.Invitee;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.InviteRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.services.InviteService;
import com.YipYapTimeAPI.YipYapTimeAPI.utils.UniqueIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class InviteServiceImpl implements InviteService {

    private InviteRepository inviteRepository;

    public InviteServiceImpl(InviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    @Override
    // Param String url: this should be a .ENV variable for the server or the frontend, which ever you choose
    // Param UUID inviter: this should be the UUID of the user who is sending the invite\
    // Param List<String> emails: this should be a list of emails that the user wants to invite
    public String generateInvite(User inviter, String url, List<String> emails) throws InviteException {
        try {
            log.info("finding existing invite for user: {}", inviter.getId());
            Optional<Invites> invite = inviteRepository.findInvitesByUserId(inviter.getId());

            // if we have an invite already created, send the link and add each user that we have invited
            if (invite.isPresent() && invite.get().getStatus() == Status.CREATED) {
            //check if email list is empty, then add onto it or if
            //email list is different than what we capture from DB
            //(use emailsToInviteesConversion method)
                //TODO: this could be a bad way to check, revisit a better way to do this
                log.info("checking if invite list is empty or has new emails to add to existing invites...");
                if(invite.get().getInvitees().isEmpty() || invite.stream().anyMatch(invitee -> invitee.getInvitees().equals(emails.get(0)))){
                    log.info("New emails found, add to invite list");
                    Set<Invitee> newInvitees = emailsToInviteesConversion(emails);
                    log.info("adding new invitees to existing invite...");
                    invite.get().getInvitees().addAll(newInvitees);
                }
                log.info("existing invite found for: {}", inviter.getId());
                return invite.get().getInvite_link();
                // add the user to the invitees list here
            }

            // otherwise, create a new invite and return the link
            log.info("creating inviteID string...");
            String inviteID = UniqueIDGenerator.generateInviteId();
            log.info("inviteID string created: {}", inviteID);

            String inviteString = url + "/" + inviteID;
            log.info("invite string created: {}", inviteString);

            log.info("creating new invite...");
            log.info("Converting emails to invitee list...");
            Invites newInvite = new Invites().builder()
                    .inviter(inviter)
                    .inviteID(inviteID)
                    .invite_link(inviteString)
                    .created_date(LocalDateTime.now())
                    .status(Status.CREATED)
                    .invitees(emailsToInviteesConversion(emails))
                    .build();

            log.info("saving new invite...");
            inviteRepository.save(newInvite);

            log.info("invite saved created!");
            return inviteString;
        } catch (Exception e) {
            log.error("Error generating invite: {}", e.getMessage());
            throw new RuntimeException("Error generating invite: " + e.getMessage());
        }
    }

    @Override
    public String getInviteID(User user) {
        log.info("finding existing invite for user: {}", user.getId());
        Optional<Invites> userInv =  inviteRepository.findInvitesByUserId(user.getId());

        log.info("checking if invite exists...");
        if(userInv.isPresent()){
            log.info("invite found for user: {}", user.getId());
            return userInv.get().getInviteID();
        }

        log.info("creating new invite for user: {}", user.getId());
        Invites invites = new Invites();

        invites.setInviter(user);
        invites.setCreated_date(LocalDateTime.now());
        invites.setStatus(Status.CREATED);
        invites.setInviteID(UniqueIDGenerator.generateInviteId());

        log.info("saving new invite for user: {}", user.getId());
        return invites.getInviteID();
    }

    private Set<Invitee> emailsToInviteesConversion (List<String> emails){
        var newInvitees = new HashSet<Invitee>();
        Invitee invitee = new Invitee();
        log.info("creating new invitees out of emails...");
        emails.forEach(email -> {
            invitee.setEmail(email);
            invitee.setStatus(Status.CREATED);
            invitee.setDate_sent(LocalDateTime.now());
            invitee.setExp_date(LocalDateTime.now().plusDays(7));
            newInvitees.add(invitee);
        });
        return newInvitees;
    }

    @Override
    public List<User> getUsersInvited(User user) {
        return null;
    }
}
