package com.chatApp.webSocketAPI.repository;

import com.chatApp.webSocketAPI.model.Chat;
import com.chatApp.webSocketAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    @Query("SELECT c from Chat c JOIN c.users u WHERE u.ID=:userID")
    public List<Chat> findChatByUserID(@Param("userID") Integer userID);

    @Query("SELECT c FROM Chat c WHERE c.isGroupChat=false AND :user MEMBER OF c.users AND :requestedUser MEMBER OF c.users")
    public Chat findSingleChatByUserID(@Param("user") User user, @Param("requestedUser")User requestedUser);

}
