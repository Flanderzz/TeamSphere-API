package com.YipYapTimeAPI.YipYapTimeAPI.repository;

import com.YipYapTimeAPI.YipYapTimeAPI.models.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository  extends JpaRepository<Messages, UUID> {

    @Query("select m from Messages m join m.chat c where c.id=:chatId order by m.timeStamp asc")
    List<Messages> findMessageByChatId(@Param("chatId") UUID chatId);

    Optional<Messages> findById(UUID userId);

    void deleteById(UUID messageId);

}

