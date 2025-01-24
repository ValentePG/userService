package dev.valente.user_service.brasilapiclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.valente.user_service.brasilapiclient.response.CepGetErrorResponse;
import dev.valente.user_service.brasilapiclient.response.CepGetResponse;
import dev.valente.user_service.config.BrasilApiConfigurationProperties;
import dev.valente.user_service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class BrasilApiService {

    private final RestClient.Builder brasilApiClient;

    private final BrasilApiConfigurationProperties properties;

    private final ObjectMapper objectMapper;

    public CepGetResponse findCep(String cep) {
        return brasilApiClient
                .build()
                .get()
                .uri(properties.cepUri(), cep)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    var body = new String(response.getBody().readAllBytes());
                    var cepGetErrorResponse = objectMapper.readValue(body, CepGetErrorResponse.class);
                    throw new NotFoundException(body);
                })
                .body(CepGetResponse.class);
    }
}
