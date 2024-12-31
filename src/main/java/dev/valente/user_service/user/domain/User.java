package dev.valente.user_service.user.domain;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private long id;

    private String firstName;

    private String lastName;

    private String email;
}
