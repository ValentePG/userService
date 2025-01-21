package dev.valente.user_service.config;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static dev.valente.user_service.common.Constants.*;

@TestConfiguration
@Lazy
public class RestAssuredConfig {
    @LocalServerPort
    int port;

    @Bean(name = "requestSpecificationRegularUser")
    public RequestSpecification requestSpecificationRegularUser() {
        return RestAssured.given()
                .baseUri(BASE_URI + port)
                .auth().preemptive().basic(REGULAR_USERNAME, REGULAR_PASSWORD);
    }

    @Bean(name = "requestSpecificationAdmin")
    public RequestSpecification requestSpecificationAdmin() {
        return RestAssured.given()
                .baseUri(BASE_URI + port)
                .auth().preemptive().basic(ADMIN_USERNAME, ADMIN_PASSWORD);
    }
}
