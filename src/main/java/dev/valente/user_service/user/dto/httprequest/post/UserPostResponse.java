package dev.valente.user_service.user.dto.httprequest.post;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserPostResponse(

        @Schema(description = "Id gerado pelo banco", example = "1")
        long id,

        @Schema(description = "Primeiro nome do Usuário", example = "Gabriel")
        String firstName,

        @Schema(description = "Segundo nome do Usuário", example = "Gomes")
        String lastName,

        @Schema(description = "Email válido do Usuário", example = "gabriel@mail.com")
        String email) {
}
