package com.YipYapTimeAPI.YipYapTimeAPI.repository;

import com.YipYapTimeAPI.YipYapTimeAPI.models.Invites;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import com.YipYapTimeAPI.YipYapTimeAPI.models.embeddables.Invitee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InviteRepository extends JpaRepository<Invites, UUID>{
    @Query("SELECT i FROM Invites i WHERE i.inviter.id = :userId")
    Optional<Invites> findInvitesByUserId(@Param("userId") UUID userId);



    @Query("SELECT i.invitees FROM Invites i WHERE i.inviter = :inviter")
    List<Invitee> findInviteesByInviter(@Param("inviter") User inviter);
}