package dev.valente.user_service.common;

import dev.valente.user_service.domain.User;
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
                .lastName("Freixo").email("joãoodoe@gmail.com").build();

        LIST_USERS.add(user1);
        LIST_USERS.add(user2);
        LIST_USERS.add(user3);
    }

    public List<User> getListUsers() {
        return LIST_USERS;
    }

    public User getUserToSave() {
        return User.builder()
                .id(1L)
                .email("geovane@gmail.com")
                .firstName("Geovane")
                .lastName("Valente")
                .build();
    }

    public User getUserToSaveWithoutId() {
        return User.builder()
                .email("geovane@gmail.com")
                .firstName("Geovane")
                .lastName("Valente")
                .build();
    }

    public User getUserToFind() {
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
                .build();
    }

    public User getNewUserWithNewFirstName() {
        return User.builder()
                .id(getFirst().getId())
                .firstName("Gabriel")
                .build();
    }

    public User getNewUserWithNewLastName() {
        return User.builder()
                .id(getFirst().getId())
                .lastName("Valente")
                .build();
    }

    public User getThirdUser() {
        return getListUsers().stream().filter(u -> u.getId() == 3).findFirst().orElse(null);
    }

    private User getFirst() {
        return getListUsers().getFirst();
    }


}
