package com.YipYapTimeAPI.YipYapTimeAPI.services;

import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendInviteEmail(String to, String addressLink, User inviter);

}
