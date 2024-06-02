package com.YipYapTimeAPI.YipYapTimeAPI.models;

import com.YipYapTimeAPI.YipYapTimeAPI.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import com.YipYapTimeAPI.YipYapTimeAPI.models.embeddables.Invitee;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invites {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime created_date;

    @OneToOne
    private User inviter;

    private String inviteID;

    // maybe replace/void this? Because we already have the inviteID
    // which we can append to the url instead of storing the irl itself
    private String invite_link;

    private Status status;

    @OneToMany
    private Set<Invitee> invitees = new HashSet<>();
}