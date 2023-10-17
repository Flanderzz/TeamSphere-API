package com.chatApp.webSocketAPI.repository;

import com.chatApp.webSocketAPI.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("SELECT m FROM Message m JOIN m.chat c WHERE c.ID=:chatID")
    public List<Message> findChatMsgByID(@Param("chatID") Integer chatID);

}
