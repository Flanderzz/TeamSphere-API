package com.YipYapTimeAPI.YipYapTimeAPI.repository;

import com.YipYapTimeAPI.YipYapTimeAPI.models.Chat;
import com.YipYapTimeAPI.YipYapTimeAPI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    @Query("select c from Chat c join c.users u where u.id=:userId")
    List<Chat> findChatByUserId(Integer userId);

    @Query("select c from Chat c Where c.is_group=false And :user Member of c.users And :reqUser Member of c.users")
    Chat findSingleChatByUsersId(@Param("user") User user, @Param("reqUser") User reqUser);
}