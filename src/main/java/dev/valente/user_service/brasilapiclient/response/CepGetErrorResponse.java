package dev.valente.user_service.brasilapiclient.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;

import java.util.List;

@Builder
public record CepGetErrorResponse(String name,
                                  String message,
                                  String type,
                                  List<CepInnerErrorResponse> errors) {
}

