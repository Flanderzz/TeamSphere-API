package co.teamsphere.api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSummaryDTO {
    private UUID id;
    private String chatName;
    private String chatImage;
    private UUID createdBy;
    private MessageDTO lastMessage;

    public ChatSummaryDTO(UUID id, String chatName, String chatImage, UUID createdBy,
                          UUID messageId, String content, LocalDateTime timeStamp,
                          boolean isRead, UUID userId, UUID chatId,
                          String otherUserName, String otherUserProfile) {
        this.id = id;
        this.chatName = chatName;
        this.chatImage = chatImage;
        this.createdBy = createdBy;
        this.lastMessage = (messageId != null)
                ? new MessageDTO(messageId, content, timeStamp, isRead, userId, chatId)
                : null;
        this.chatName = otherUserName;
        this.chatImage = otherUserProfile;
    }
}

