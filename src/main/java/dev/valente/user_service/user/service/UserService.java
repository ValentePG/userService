package dev.valente.user_service.user.service;

import dev.valente.user_service.domain.User;
import dev.valente.user_service.exception.EmailAlreadyExist;
import dev.valente.user_service.exception.NotFoundException;
import dev.valente.user_service.user.mapper.UserMapper;
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
        assertEmailDoesNotExists(user.getEmail());
        return userRepository.save(user);
    }

    public void delete(long id) {
        var user = findByIdOrThrowNotFound(id);
        userRepository.delete(user);
    }

    public void replace(User userToUpdate) {
        var userSaved = assertUserExists(userToUpdate.getId());

        findNullToReplace(userSaved, userToUpdate);

        userRepository.save(userToUpdate);
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

    private void findNullToReplace(User userSaved, User userToUpdate) {
        if (userToUpdate.getEmail() != null) assertEmailDoesNotExists(userToUpdate.getEmail(), userToUpdate.getId());
        if (userToUpdate.getPassword() == null) userToUpdate.setPassword(userSaved.getPassword());
        if (userToUpdate.getFirstName() == null) userToUpdate.setFirstName(userSaved.getFirstName());
        if (userToUpdate.getLastName() == null) userToUpdate.setLastName(userSaved.getLastName());
        if (userToUpdate.getEmail() == null) userToUpdate.setEmail(userSaved.getEmail());
        userToUpdate.setRoles(userSaved.getRoles());
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
