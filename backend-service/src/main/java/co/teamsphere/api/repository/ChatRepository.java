package co.teamsphere.api.repository;

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

    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId")
    Page<Chat> findChatsByUserId(@Param("userId") UUID userId, Pageable pageable);


    @Query("select c from Chat c Where c.isGroup=false And :user Member of c.users And :reqUser Member of c.users")
    Chat findSingleChatByUsersId(@Param("user") User user, @Param("reqUser") User reqUser);

    Optional<Chat> findById(UUID chatId);
    void deleteById(UUID chatId);
}