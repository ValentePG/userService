package dev.valente.user_service.user.dto.httprequest.put;

public record UserPutRequest(long id, String firstName, String lastName, String email) {
}
