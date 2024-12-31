package dev.valente.user_service.user.dto.httprequest.get;

public record UserGetResponse(long id, String firstName, String lastName, String email) {
}
