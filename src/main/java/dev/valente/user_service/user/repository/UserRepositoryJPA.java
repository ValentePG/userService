package dev.valente.user_service.user.repository;

import dev.valente.user_service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryJPA extends JpaRepository<User, Long> {
    Optional<User> findUserByFirstName(String firstName);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByEmailAndIdNot(String email, Long id);
}
