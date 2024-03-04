package com.YipYapTimeAPI.YipYapTimeAPI.repository;

import com.YipYapTimeAPI.YipYapTimeAPI.models.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository  extends JpaRepository<Messages, Long> {

    @Query("select m from Messages m join m.chat c where c.id=:chatId")
    List<Messages> findMessageByChatId(@Param("chatId") Integer chatId);

}

