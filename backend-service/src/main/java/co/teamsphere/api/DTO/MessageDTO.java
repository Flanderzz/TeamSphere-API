package co.teamsphere.api.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MessageDTO {
    private UUID id;
    private String content;
    private LocalDateTime timeStamp;
    private Boolean isRead;
    private UUID userId;
    private UUID chatId;
}
