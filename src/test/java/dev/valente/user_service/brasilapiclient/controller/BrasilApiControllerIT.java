package dev.valente.user_service.brasilapiclient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import dev.valente.user_service.common.FileUtil;
import dev.valente.user_service.config.IntegrationTestConfig;
import dev.valente.user_service.config.RestAssuredConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = RestAssuredConfig.class)
@EnableWireMock({
        @ConfigureWireMock(port = 0, filesUnderClasspath = "wiremock/brasil-api/cep")})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(value = "/sql/init_one_login_regular_user.sql")
@Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BrasilApiControllerIT extends IntegrationTestConfig {

    private static final String URL = "/v1/brasil-api/cep";

    @Autowired
    private FileUtil fileUtil;

//    @InjectWireMock
//    private WireMockServer wireMockServer;

    @Autowired
    @Qualifier(value = "requestSpecificationRegularUser")
    private RequestSpecification requestSpecificationRegularUser;

    @BeforeEach
    void setUrl() {
        RestAssured.requestSpecification = requestSpecificationRegularUser;
    }

    @Order(1)
    @Test
    @DisplayName("findCep returns CepGetResponse when successful")
    void findCep_ReturnsCepGetResponse_WhenSuccessful() {
        var cep = "00000000";
        var expectedResponse = fileUtil.readFile("brasil-api/cep/expected-get-cep-response-200.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/{cep}", cep)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();
    }

    @Order(2)
    @Test
    @DisplayName("findCep returns CepInnerErrorResponse when Failed")
    void findCep_ReturnsCepInnerErrorResponse_WhenUnsucessfull() {

        var cep = "400999";
        var expectedResponse = fileUtil.readFile("brasil-api/cep/expected-get-cep-response-404.json");
        var expectedMessages = List.of(
                "message",
                "CEP deve conter exatamente 8 caracteres.",
                "type",
                "validation_error",
                "name",
                "CepPromiseError",
                "errors",
                "CEP informado possui mais do que 8 caracteres.",
                "service",
                "cep_validation"
        );

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/{cep}", cep)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        expectedMessages.forEach(msg -> JsonAssertions.assertThatJson(response)
                .node("message").isString().contains(msg));
    }
}