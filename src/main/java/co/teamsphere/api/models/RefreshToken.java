package co.teamsphere.api.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import java.util.UUID;

public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int refreshTokenVersion;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
