package com.YipYapTimeAPI.YipYapTimeAPI.services.impl;

import com.YipYapTimeAPI.YipYapTimeAPI.enums.Status;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.InviteException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Invites;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.models.embeddables.Invitee;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.InviteRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.InviteeRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.services.ChatService;
import com.YipYapTimeAPI.YipYapTimeAPI.services.InviteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class InviteServiceImpl implements InviteService {
    private final InviteeRepository inviteeRepository;
    private InviteRepository inviteRepository;
    private ChatService chatService;

    public InviteServiceImpl(InviteRepository inviteRepository,
                             InviteeRepository inviteeRepository,
                             ChatService chatService) {
        this.inviteRepository = inviteRepository;
        this.inviteeRepository = inviteeRepository;
        this.chatService = chatService;
    }

    @Override
    // Param String url: this should be a .ENV variable for the server or the frontend, which ever you choose
    // Param UUID inviter: this should be the UUID of the user who is sending the invite\
    // Param List<String> emails: this should be a list of emails that the user wants to invite
    public String generateInvite(User inviter, String ID, List<String> emails) throws InviteException {
        try {
            log.info("finding existing invite for user: {}", inviter.getId());
            Optional<Invites> userInv = inviteRepository.findInvitesByInvite_ID(ID);

            log.info("checking if invite exists...");
            if (userInv.isEmpty()){
                log.error("No invite found for user: {}", inviter.getId());
                throw new InviteException("ID is invalid");
            }

            log.info("checking if invite is stopped for {}...", inviter.getId());
            if (userInv.get().getStatus().equals(Status.STOPPED)){
                throw new InviteException("Inviter has stopped all invites. \nPlease reach out to them for a new invite");
            }

            // might be redundant
            log.info("checking if invite is at capacity for {}...", inviter.getId());
            if (userInv.get().getInvitees().size() >= 100){
                throw new InviteException("You have reached the max Number of invites.\nTry deleting some and trying again");
            }

            // TODO: check if we have new emails, if so, add them too our invitee list keeping in mind to check for capacity
            Set<Invitee> invitees = userInv.get().getInvitees();
            for (var email : emails) {
                boolean emailExists = invitees.stream().anyMatch(invitee -> invitee.getEmail().equals(email));
                if (!emailExists) {
                    if (invitees.size() >= 100) {
                        throw new InviteException("You have reached the max number of invites.\nTry deleting or canceling some and trying again");
                    }
                    Invitee newInvitee = emailsToInviteesConversion(email);
                    invitees.add(newInvitee);
                    inviteeRepository.save(newInvitee);
                }
            }

            userInv.get().setInvitees(invitees);

            //maybe save/replace userInv here?

            return userInv.get().getInvite_link();
        } catch (InviteException e){
            throw new InviteException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("an error has occurred");
        }
    }

    @Override
    public Chat connectUsers(User invitee, String ID) throws InviteException, UserException {
        log.info("finding existing invite for user: {}", invitee.getId());
        Optional<Invites> invite = inviteRepository.findInvitesByInvite_ID(ID);

        log.info("checking if invite exists...");
        if (invite.isEmpty()) {
            log.error("Invite not found");
            throw new InviteException("Invite not found");
        }

        log.info("checking if invite is stopped...");
        if (invite.get().getStatus().equals(Status.STOPPED)) {
            log.error("Invite is stopped");
            throw new InviteException("Invite is no longer active");
        }

        log.info("checking if invitee is in invite...");
        if (invite.get().getInvitees().stream().noneMatch(invitee1 -> invitee1.getEmail().equals(invitee.getEmail()))) {
            log.error("Invitee not found");
            throw new InviteException("Invitee not found");
        }

        return chatService.createChat(invitee.getId(), invite.get().getInviter().getId(), false);
    }

    @Override
    public String getInviteID(User user) {
        log.info("finding existing invite for user: {}", user.getId());
        Optional<Invites> userInv =  inviteRepository.findInvitesByUserId(user.getId().toString());

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
        invites.setInviteID(UUID.randomUUID().toString());
        invites.setInvite_link("https://teamsphere.co/" + invites.getInviteID());

        log.info("saving new invite for user: {}", user.getId());
        inviteRepository.save(invites);
        return invites.getInviteID();
    }

    private Invitee emailsToInviteesConversion (String email) {
        Invitee invitee = new Invitee();
        log.info("creating new invitees out of emails...");
        invitee.setEmail(email);
        invitee.setStatus(Status.CREATED);
        invitee.setDate_sent(LocalDateTime.now());
        invitee.setExp_date(LocalDateTime.now().plusDays(7));
        inviteeRepository.save(invitee);
        return invitee;
    }

    @Override
    public void pauseInvites(User user) throws InviteException {
        try {
            log.info("finding existing invite for user: {}", user.getId());
            Optional<Invites> userInv = inviteRepository.findInvitesByUserId(user.getId().toString());

            log.info("checking if invite exists...");
            if (userInv.isEmpty()) {
                throw new InviteException("No invite found for user: " + user.getId());
            }

            log.info("invite found for user: {}", user.getId());

            userInv.get().setStatus(Status.STOPPED);

            userInv.get().getInvitees().forEach(invitee -> invitee.setStatus(Status.EXPIRED));

            inviteRepository.save(userInv.get());
        } catch (InviteException e) {
            throw new InviteException(e.getMessage());
        } catch (Exception e){
            log.error("Error pausing invites: {}", e.getMessage());
            throw new RuntimeException("Error pausing invites: " + e.getMessage());
        }
    }

    @Override
    public Set<Invitee> getUsersInvited(User user) throws InviteException {
        Optional<Invites> userInv = inviteRepository.findInvitesByUserId(user.getId().toString());

        if (userInv.isEmpty()){
            throw new InviteException("no invites found for user: {}" +  user.getUsername());
        }

        return userInv.get().getInvitees();
    }
}
