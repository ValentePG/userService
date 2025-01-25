package dev.valente.user_service.brasilapiclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.valente.user_service.brasilapiclient.response.CepGetErrorResponse;
import dev.valente.user_service.brasilapiclient.response.CepGetResponse;
import dev.valente.user_service.config.BrasilApiConfigurationProperties;
import dev.valente.user_service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Log4j2
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
                    System.out.println(body);
                    var cepGetErrorResponse = objectMapper.readValue(body, CepGetErrorResponse.class);
                    System.out.println(cepGetErrorResponse);
                    throw new NotFoundException(cepGetErrorResponse.toString());
                })
                .body(CepGetResponse.class);
    }
}
