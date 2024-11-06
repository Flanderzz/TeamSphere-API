package co.teamsphere.teamsphere.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class ChatDTO {
    private UUID id;
    private String chatName;
    private String chatImage;
    private Boolean isGroup;
    private Set<UUID> admins;
    private UUID createdBy;
    private Set<UUID> users;
    private List<UUID> messages;
}
