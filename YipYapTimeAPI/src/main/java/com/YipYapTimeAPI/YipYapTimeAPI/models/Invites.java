package com.YipYapTimeAPI.YipYapTimeAPI.models;

import com.YipYapTimeAPI.YipYapTimeAPI.Enumeration.InviteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invites {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    private User inviter;

    @Column(name = "invitation_id")
    private String invite_id;
    private String invite_link;

    @Enumerated(EnumType.STRING)
    private InviteStatus status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "invitation_id")
    private List<User> recipients;
}