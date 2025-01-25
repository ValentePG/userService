package dev.valente.user_service.brasilapiclient.response;


import lombok.Builder;

@Builder
public record CepInnerErrorResponse(String name,
                                    String message,
                                    String service) {
}

