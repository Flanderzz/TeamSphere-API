package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.exception.InviteException;
import com.YipYapTimeAPI.YipYapTimeAPI.exception.UserException;
import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.models.embeddables.Invitee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface InviteService {
    String generateInvite(User inviter, String url, List<String> emails) throws InviteException;

    String getInviteID(User user);

    Chat connectUsers(User invitee, String ID) throws InviteException, UserException;

    void pauseInvites(User user) throws InviteException;

    Set<Invitee> getUsersInvited(User user) throws InviteException;
}
