package dev.valente.user_service.user.dto.httprequest.post;

import jakarta.validation.constraints.NotBlank;

public record UserPostRequest(
        @NotBlank(message = "FirstName não pode estar em branco") String firstName,
        @NotBlank(message = "LastName não pode estar em branco") String lastName,
        @NotBlank(message = "Email não pode estar em branco") String email) {
}
