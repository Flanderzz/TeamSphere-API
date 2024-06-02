package com.YipYapTimeAPI.YipYapTimeAPI.repository;

import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.models.embeddables.Invitee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InviteeRepository extends JpaRepository<Invitee, Long> {
    // ngl, this might be bad code? but f* it atp, there is no other way for
    // check if an email has already been sent through the inviter
    @Query("SELECT i FROM Invitee i JOIN Invites inv ON i MEMBER OF inv.invitees WHERE i.email = :email AND inv.inviter = :inviter")
    Optional<Invitee> findInviteeByEmailAndInviter(@Param("email") String email, @Param("inviter") User inviter);
}
