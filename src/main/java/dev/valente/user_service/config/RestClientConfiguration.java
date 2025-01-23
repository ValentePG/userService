package dev.valente.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfiguration {

    private final BrasilApiConfigurationProperties properties;

    @Bean(name = "brasilApiClient")
    public RestClient.Builder brasilApiClient() {
        return RestClient.builder()
                .baseUrl(properties.baseUrl());
    }
}
