package dev.valente.user_service.user.service;

import dev.valente.user_service.user.domain.User;
import dev.valente.user_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

        if(user.getEmail() == null){
            user.setEmail(oldUser.getEmail());
        }

        if(user.getFirstName() == null){
            user.setFirstName(oldUser.getFirstName());
        }

        if(user.getLastName() == null){
            user.setLastName(oldUser.getLastName());
        }

        userRepository.replace(oldUser, user);
    }

    public User findByIdOrThrowNotFound(long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User findByNameOrThrowNotFound(String name){
        return userRepository.findByFirstName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User findByEmailOrThrowNotFound(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

}
