package dev.valente.user_service.user.service;

import dev.valente.user_service.exception.NotFoundException;
import dev.valente.user_service.user.domain.User;
import dev.valente.user_service.user.repository.UserRepository;
import dev.valente.user_service.user.repository.UserRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryJPA userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(long id) {
        var user = findByIdOrThrowNotFound(id);
        userRepository.delete(user);
    }

    public void replace(User user) {
        var oldUser = findByIdOrThrowNotFound(user.getId());

        findNullToReplace(oldUser, user);

        userRepository.save(oldUser);
    }

    public User findByIdOrThrowNotFound(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User findByFirstNameOrThrowNotFound(String name) {
        return userRepository.findUserByFirstName(name)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User findByEmailOrThrowNotFound(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void findNullToReplace(User oldUser, User user) {
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
            log.info("Usuário não pretende trocar o email");
        }

        if (user.getFirstName() != null) {
            oldUser.setFirstName(user.getFirstName());
            log.info("Usuário não pretende trocar o primeiro nome");
        }
        if (user.getLastName() != null) {
            oldUser.setLastName(user.getLastName());
            log.info("Usuário não pretende trocar o ultimo nome");
        }
    }

}
