package com.chatApp.webSocketAPI.repository;

import com.chatApp.webSocketAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository <User, Integer> {

    public User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:query% or u.email LIKE %:query%")
    public List<User> searchUser(@Param("query") String query);

}
