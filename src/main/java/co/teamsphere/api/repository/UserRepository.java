package co.teamsphere.api.repository;

import co.teamsphere.api.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository  extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username LIKE CONCAT('%', :name, '%')")
    List<User> searchUsers(@Param("name") String name);


//    @Nullable
//    Optional<User> findById(UUID userId);
}