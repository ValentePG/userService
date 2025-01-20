package dev.valente.user_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static dev.valente.user_service.common.Constants.*;
import static dev.valente.user_service.common.Constants.REGULAR_PASSWORD;
import static dev.valente.user_service.common.Constants.REGULAR_USERNAME;

@TestConfiguration
@Lazy
public class TestRestTemplateConfig {

    @LocalServerPort
    int port;

    @Bean
    public TestRestTemplate testRestTemplate() {
        var uri = new DefaultUriBuilderFactory(BASE_URI + port);

        var testRestTemplate = new TestRestTemplate()
                .withBasicAuth(REGULAR_USERNAME, REGULAR_PASSWORD);
        testRestTemplate.setUriTemplateHandler(uri);

        return testRestTemplate;
    }
}
