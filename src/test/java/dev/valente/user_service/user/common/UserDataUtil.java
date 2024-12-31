package dev.valente.user_service.user.common;

import dev.valente.user_service.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDataUtil {
    private final List<User> LIST_USERS = new ArrayList<>();

    {
        var user1 = User.builder().id(1L).firstName("Johny")
                .lastName("Juscelino").email("jonydoe@gmail.com").build();

        var user2 = User.builder().id(2L).firstName("Jorge")
                .lastName("Frigs").email("jorgedoe@gmail.com").build();

        var user3 = User.builder().id(3L).firstName("João")
                .lastName("freixo").email("joãoodoe@gmail.com").build();

        LIST_USERS.add(user1);
        LIST_USERS.add(user2);
        LIST_USERS.add(user3);
    }

    public List<User> getListUsers() {
        return LIST_USERS;
    }

    public User getUserToSave(){
        return User.builder()
                .id(15L)
                .email("geovane@gmail.com")
                .firstName("Geovane")
                .lastName("Valente")
                .build();
    }

    public User getUserToFind(){
        return getFirst();
    }

    public User getUserToReplace() {
        return getFirst();
    }

    public User getUserToDelete() {
        return getFirst();
    }

    public User getNewUserWithNewEmail() {
        return User.builder()
                .id(getFirst().getId())
                .email("geovane321@gmail.com")
                .firstName(getFirst().getFirstName())
                .lastName(getFirst().getLastName())
                .build();
    }

    public User getNewUserWithNewFirstName() {
        return User.builder()
                .id(getFirst().getId())
                .email(getFirst().getEmail())
                .firstName("Gabriel")
                .lastName(getFirst().getLastName())
                .build();
    }

    public User getNewUserWithNewLastName() {
        return User.builder()
                .id(getFirst().getId())
                .email(getFirst().getEmail())
                .firstName(getFirst().getFirstName())
                .lastName("Valente")
                .build();
    }

    private User getFirst(){
        return getListUsers().getFirst();
    }
}
