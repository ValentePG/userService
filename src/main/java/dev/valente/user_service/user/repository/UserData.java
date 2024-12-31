package dev.valente.user_service.user.repository;

import dev.valente.user_service.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserData {

    private final List<User> LIST_USERS = new ArrayList<>();

    {
        var user1 = User.builder().id(1L).firstName("Gabriel")
                .lastName("Gomes").email("gabrieldoe@gmail.com").build();

        var user2 = User.builder().id(2L).firstName("Gustavo")
                .lastName("Jorge").email("gustavodoe@gmail.com").build();

        var user3 = User.builder().id(3L).firstName("Ronaldo")
                .lastName("da silva").email("ronaldodoe@gmail.com").build();

        LIST_USERS.add(user1);
        LIST_USERS.add(user2);
        LIST_USERS.add(user3);
    }

    public List<User> getListUsers() {
        return LIST_USERS;
    }
}
