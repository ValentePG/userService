package dev.valente.user_service.brasilapiclient.response;


import lombok.Builder;

import java.util.List;

@Builder
public record CepGetErrorResponse(String name, String message, String type, List<CepInnerErrorResponse> errors) {
}

