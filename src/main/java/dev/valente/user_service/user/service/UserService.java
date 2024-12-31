package dev.valente.user_service.user.service;

import dev.valente.user_service.user.domain.User;
import dev.valente.user_service.user.repository.UserRepository;
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

    private final UserRepository userRepository;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public void delete(long id){
        var user = findByIdOrThrowNotFound(id);
        userRepository.delete(user);
    }

    public void replace(User user){
        var oldUser = findByIdOrThrowNotFound(user.getId());

        findNullToReplace(oldUser, user);

        userRepository.replace(oldUser, user);
    }

    public User findByIdOrThrowNotFound(long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User findByFirstNameOrThrowNotFound(String name){
        return userRepository.findByFirstName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User findByEmailOrThrowNotFound(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void findNullToReplace(User oldUser, User user){
        if(user.getEmail() == null){
            user.setEmail(oldUser.getEmail());
            log.info("Usuário não pretende trocar o email");
        }

        if(user.getFirstName() == null){
            user.setFirstName(oldUser.getFirstName());
            log.info("Usuário não pretende trocar o primeiro nome");
        }
        if(user.getLastName() == null){
            user.setLastName(oldUser.getLastName());
            log.info("Usuário não pretende trocar o ultimo nome");
        }
    }

}
