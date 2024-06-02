package com.YipYapTimeAPI.YipYapTimeAPI.models.embeddables;

import com.YipYapTimeAPI.YipYapTimeAPI.enums.Status;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.core.SpringVersion;

import java.time.LocalDateTime;

//TODO: make this an entity instead of an embeddable
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitee {
    @Id
    private Long id;

    @Email
    private String email;
    private Status status;
    private LocalDateTime date_sent;
    private LocalDateTime exp_date;
}
