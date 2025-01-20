package dev.valente.user_service.user.dto.httprequest.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserPostRequest(
        @Schema(description = "Primeiro nome do Usuário", example = "Gabriel")
        @NotBlank(message = "FirstName não pode estar em branco")
        String firstName,

        @Schema(description = "Segundo nome do usuário", example = "Gomes")
        @NotBlank(message = "LastName não pode estar em branco")
        String lastName,

        @Schema(description = "Email válido do usuário", example = "gabriel@mail.com")
        @NotBlank(message = "Email não pode estar em branco")
        String email,

        @NotBlank(message = "Campo senha não pode estar em branco")
        String password){
}
