package dev.valente.user_service.user.repository;

import dev.valente.user_service.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final UserData userData;

    public User save(User user) {
        userData.getListUsers().add(user);
        return user;
    }

    public void replace(User oldUser, User newUser) {
        delete(oldUser);
        save(newUser);
    }

    public void delete(User user) {
        userData.getListUsers().remove(user);
    }

    public List<User> findAll() {
        return userData.getListUsers();
    }

    public Optional<User> findByEmail(String email) {
        return userData.getListUsers().stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public Optional<User> findById(long id) {
        return userData.getListUsers().stream().filter(user -> user.getId() == id).findFirst();
    }

    public Optional<User> findByFirstName(String name) {
        return userData.getListUsers().stream().filter(user -> user.getFirstName().equalsIgnoreCase(name)).findFirst();
    }


}
