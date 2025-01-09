package dev.valente.user_service.user.service;

import dev.valente.user_service.exception.EmailAlreadyExist;
import dev.valente.user_service.exception.NotFoundException;
import dev.valente.user_service.user.domain.User;
import dev.valente.user_service.user.repository.UserRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        assertEmailDoesNotExists(user.getEmail());
        return userRepository.save(user);
    }

    public void delete(long id) {
        var user = findByIdOrThrowNotFound(id);
        userRepository.delete(user);
    }

    public void replace(User user) {
        var oldUser = assertUserExists(user.getId());

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
            assertEmailDoesNotExists(user.getEmail(), user.getId());
            oldUser.setEmail(user.getEmail());
            log.info("Usuário trocou de email");
        }
        if (user.getFirstName() != null) {
            oldUser.setFirstName(user.getFirstName());
            log.info("Usuário trocou o primeiro nome");
        }
        if (user.getLastName() != null) {
            oldUser.setLastName(user.getLastName());
            log.info("Usuário trocou o ultimo nome");
        }
    }

    private User assertUserExists(Long id) {
        return findByIdOrThrowNotFound(id);
    }

    private void assertEmailDoesNotExists(String email) {
        userRepository.findUserByEmail(email)
                .ifPresent(this::throwEmailExistsException);
    }

    private void assertEmailDoesNotExists(String email, Long id) {
        userRepository.findUserByEmailAndIdNot(email, id)
                .ifPresent(this::throwEmailExistsException);
    }

    private void throwEmailExistsException(User user) {
        throw new EmailAlreadyExist("Email %s already exists".formatted(user.getEmail()));
    }

}
