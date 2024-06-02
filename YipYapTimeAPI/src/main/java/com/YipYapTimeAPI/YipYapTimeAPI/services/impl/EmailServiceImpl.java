package com.YipYapTimeAPI.YipYapTimeAPI.services.impl;

import com.YipYapTimeAPI.YipYapTimeAPI.enums.Status;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Invites;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.models.embeddables.Invitee;
import com.YipYapTimeAPI.YipYapTimeAPI.repository.InviteeRepository;
import com.YipYapTimeAPI.YipYapTimeAPI.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final InviteeRepository inviteeRepository;
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String from;

    public EmailServiceImpl(JavaMailSender javaMailSender, InviteeRepository inviteeRepository){
        this.javaMailSender = javaMailSender;
        this.inviteeRepository = inviteeRepository;
    }

    @Override
    public void sendInviteEmail(String to, String addressLink, User inviter) {
        try {
            //TODO: before we do this, check if email is already sent
            Optional<Invitee> invitees = inviteeRepository.findInviteeByEmailAndInviter(to, inviter);

            if (invitees.isPresent() && invitees.get().getStatus().equals(Status.SENT)){
                return;
            }

            SimpleMailMessage msg = new SimpleMailMessage();
            log.info("sending invite email for {}", inviter.getEmail());
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject("You Have Been Invited to chat with "+inviter.getUsername()+" on TeamSphere!");
            msg.setText(inviter.getEmail() + " has invited you to chat with them on Teamsphere. \nPlease join using this link: " + addressLink);
            log.info("sent email to {}", to);
            javaMailSender.send(msg);

            // TODO: edit invitee status to sent
            // if they already have an account, update status to sent.
            // if they dont have an account, send them to sign-in and retry the link.
            // also this might not work, but we will see.
            if (invitees.isPresent()){
                Invitee invitee = invitees.get();
                invitee.setStatus(Status.SENT);
                inviteeRepository.save(invitee);
            }
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
            // potential change, this might be the wrong exception to throw
            throw new RuntimeException("Error sending email" + e.getMessage());
        }
    }
}
