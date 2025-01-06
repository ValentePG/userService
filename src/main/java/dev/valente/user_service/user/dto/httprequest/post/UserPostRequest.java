package dev.valente.user_service.user.dto.httprequest.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record UserPostRequest(
        @NotEmpty(message = "FirstName não deve estar vazio")
        @NotBlank(message = "FirstName não pode estar em branco") String firstName,

        @NotEmpty(message = "LastName não deve estar vazio")
        @NotBlank(message = "LastName não pode estar em branco") String lastName,

        @NotEmpty(message = "Email não deve estar vazio")
        @NotBlank(message = "Email não pode estar em branco") String email) {
}
