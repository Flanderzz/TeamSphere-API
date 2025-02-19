package co.teamsphere.api.repository;

import co.teamsphere.api.DTO.ChatSummaryDTO;
import co.teamsphere.api.models.Chat;
import co.teamsphere.api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {
    @Query("""
    SELECT new co.teamsphere.api.DTO.ChatSummaryDTO(
        c.id,
        c.chatName,
        c.chatImage,
        c.createdBy.id,
        m.id,
        m.content,
        m.timeStamp,
        m.isRead,
        m.username.id,
        m.chat.id,
        COALESCE((SELECT u.username FROM c.users u WHERE u.id <> :userId AND c.isGroup = false), ''),
        COALESCE((SELECT u.profilePicture FROM c.users u WHERE u.id <> :userId AND c.isGroup = false), '')
    )
    FROM Chat c
    JOIN c.users u
    LEFT JOIN c.messages m ON m.timeStamp = (
        SELECT MAX(m2.timeStamp) FROM Messages m2 WHERE m2.chat.id = c.id
    )
    WHERE u.id = :userId
    GROUP BY c, m.id, m.content, m.isRead, m.username.id, m.chat.id
    ORDER BY MAX(m.timeStamp) DESC
""")
    Page<ChatSummaryDTO> findChatsByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("select c from Chat c Where c.isGroup=false And :user Member of c.users And :reqUser Member of c.users")
    Chat findSingleChatByUsersId(@Param("user") User user, @Param("reqUser") User reqUser);

    Optional<Chat> findById(UUID chatId);
    void deleteById(UUID chatId);
}