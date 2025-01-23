package dev.valente.user_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "brasil-api")
public record BrasilApiConfigurationProperties(String baseUrl, String cepUri) {
    // RestTemplate, WebClient, RestClient, Apache Camel, Feign, Retrofit, Okhttp, Jersey, RestEasy
}
