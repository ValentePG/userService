package dev.valente.user_service.profile.dto.post;

import jakarta.validation.constraints.NotBlank;

public record ProfilePostRequest(

        @NotBlank(message = "O campo nome não pode estar vazio") String name,
        @NotBlank(message = "O campo descrição não pode estar vazio") String description) {
}
