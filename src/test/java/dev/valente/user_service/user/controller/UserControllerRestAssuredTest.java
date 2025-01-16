package dev.valente.user_service.user.controller;

import dev.valente.user_service.common.FileUtil;
import dev.valente.user_service.common.UserDataUtil;
import dev.valente.user_service.config.IntegrationTestConfig;
import dev.valente.user_service.user.repository.UserRepositoryJPA;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@ComponentScan(basePackages = {"dev.valente.user_service.user", "dev.valente.user_service.common"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerRestAssuredTest extends IntegrationTestConfig {

    private final String URL = "/v1/users";

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private UserDataUtil userDataUtil;

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepositoryJPA userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("GET v1/users should return list of all users")
    @Order(1)
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_shouldReturnListOfUsers_whenSuccessfull() {

        var pathResponse = "/user/get/get_findallusers_200.json";

        var expectedResponse = fileUtil.readFile(pathResponse);

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .and(users -> {
                    users.node("[0].id").asNumber().isPositive();
                    users.node("[1].id").asNumber().isPositive();
                    users.node("[2].id").asNumber().isPositive();
                });

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("[*].id")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET v1/users should return empty list")
    @Order(2)
    void findAll_shouldReturnEmptyList_whenSuccessfull() {

        var pathResponse = "/user/get/get_findallusers-empty_200.json";

        var expectedResponse = fileUtil.readFile(pathResponse);

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(expectedResponse))
                .log().all();

    }

    @Test
    @DisplayName("GET v1/users/{existentId} should return a user")
    @Order(3)
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById_shouldReturnUser_whenSuccessfull() throws Exception {
        var pathResponse = "/user/get/get_findbyid_200.json";
        var expectedUserFromFile = fileUtil.readFile(pathResponse);

        var userExpected = userRepository.findUserByFirstName("Johny");

        Assertions.assertThat(userExpected)
                .isNotEmpty()
                .get().hasFieldOrProperty("id");

        var id = userExpected.get().getId();

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", id)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedUserFromFile);


    }

    @Test
    @DisplayName("GET v1/users/{inexistentId} should return NOT FOUND")
    @Order(4)
    void findById_shouldReturnNotFound_whenFailed() throws Exception {

        var response = fileUtil.readFile("/user/get/get_findbyid-inexistentid_404.json");

        var inexistentId = 50L;

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", inexistentId)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();


    }

    @Test
    @DisplayName("GET v1/users/find?email=existentEmail should return a user")
    @Order(5)
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findWithParamsWithEmail_shouldReturnUser_whenSuccessfull() throws Exception {
        var pathResponse = "/user/get/get_findbyemail_200.json";
        var expectedUserFromFile = fileUtil.readFile(pathResponse);

        var userExpected = userDataUtil.getUserToFind();
        var email = userExpected.getEmail();

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .queryParam("email", email)
                .get(URL + "/find")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedUserFromFile);

    }

    @Test
    @DisplayName("GET v1/users/find?email=inexistentEmail should return NOT FOUND")
    @Order(6)
    void findWithParamsWithNewEmail_shouldReturnNotFound_whenFailed() throws Exception {

        var response = fileUtil.readFile("/user/get/get_findbyemail-inexistentemail_404.json");

        var inexistentEmail = "inexistent@gmail.com";

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .queryParam("email", inexistentEmail)
                .get(URL + "/find")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/users/find?firstName=existentFirstName should return a user")
    @Order(7)
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findWithParamsWithFirstName_shouldReturnUser_whenSuccessfull() throws Exception {
        var pathResponse = "/user/get/get_findbyfirstname_200.json";
        var expectedUserFromFile = fileUtil.readFile(pathResponse);

        var userExpected = userDataUtil.getUserToFind();
        var firstName = userExpected.getFirstName();

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .queryParam("firstName", firstName)
                .get(URL + "/find")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedUserFromFile);

    }

    @Test
    @DisplayName("GET v1/users/find?firstName=inexistentFirstName should return NOT FOUND")
    @Order(8)
    void findWithParamsWithFirstName_shouldReturnNotFound_whenFailed() throws Exception {

        var response = fileUtil.readFile("/user/get/get_findbyfirstname-inexistfirstname_404.json");

        var inexistentFirstName = "Inexistent";

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .queryParam("firstName", inexistentFirstName)
                .get(URL + "/find")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();

    }

}
