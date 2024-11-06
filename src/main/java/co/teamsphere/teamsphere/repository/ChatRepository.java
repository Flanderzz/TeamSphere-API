package co.teamsphere.teamsphere.repository;

import co.teamsphere.teamsphere.models.Chat;
import co.teamsphere.teamsphere.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    @Query("select c from Chat c join c.users u where u.id=:userId")
    List<Chat> findChatByUserId(UUID userId);

    @Query("select c from Chat c Where c.isGroup=false And :user Member of c.users And :reqUser Member of c.users")
    Chat findSingleChatByUsersId(@Param("user") User user, @Param("reqUser") User reqUser);

    Optional<Chat> findById(UUID chatId);
    void deleteById(UUID chatId);
}