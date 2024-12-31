package dev.valente.user_service.user.dto.httprequest.post;

public record UserPostResponse(long id, String firstName, String lastName, String email) {
}
