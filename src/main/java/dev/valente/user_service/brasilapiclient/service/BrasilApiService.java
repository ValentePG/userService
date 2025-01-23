package dev.valente.user_service.brasilapiclient.service;

import dev.valente.user_service.brasilapiclient.response.CepGetResponse;
import dev.valente.user_service.config.BrasilApiConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class BrasilApiService {

    private final RestClient.Builder brasilApiClient;

    private final BrasilApiConfigurationProperties properties;

    public CepGetResponse findCep(String cep) {
        return brasilApiClient
                .build()
                .get()
                .uri(properties.cepUri(), cep)
                .retrieve()
                .body(CepGetResponse.class);
    }
}
