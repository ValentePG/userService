package dev.valente.user_service.brasilapiclient.response;

public record CepGetResponse(String cep, String state, String city, String neighborhood, String street, String service) {
}
