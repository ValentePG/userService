package dev.valente.user_service.user.dto.httprequest.put;

import dev.valente.user_service.annotation.SingleFieldNotNull;
import jakarta.validation.constraints.Email;

//@SingleFieldNotNull(message = "Pelo menos um campo precisa estar preenchido corretamente")
public record UserPutRequest(long id,
                             String firstName,
                             String lastName,
                             String email,
                             String password) {
}
//@Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
//        message = "Email inválido")