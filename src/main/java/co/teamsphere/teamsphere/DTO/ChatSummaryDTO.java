package co.teamsphere.teamsphere.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChatSummaryDTO {
    private UUID id;
    private String chatName;
    private String chatImage;
    private UUID createdBy;
    private MessageDTO lastMessage;
}
