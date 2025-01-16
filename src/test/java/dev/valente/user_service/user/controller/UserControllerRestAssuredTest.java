package dev.valente.user_service.user.controller;

import dev.valente.user_service.common.FileUtil;
import dev.valente.user_service.common.UserDataUtil;
import dev.valente.user_service.config.IntegrationTestConfig;
import dev.valente.user_service.domain.User;
import dev.valente.user_service.user.repository.UserRepositoryJPA;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

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
    void findById_shouldReturnUser_whenSuccessfull() {
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
    void findById_shouldReturnNotFound_whenFailed() {

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
    void findWithParamsWithEmail_shouldReturnUser_whenSuccessfull() {
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
    void findWithParamsWithNewEmail_shouldReturnNotFound_whenFailed() {

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
    void findWithParamsWithFirstName_shouldReturnUser_whenSuccessfull() {
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
    void findWithParamsWithFirstName_shouldReturnNotFound_whenFailed() {

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

    @Test
    @DisplayName("POST v1/users should save and return a user")
    @Order(9)
    void save_shouldSaveAndReturnUser_whenSuccessfull() {
        var pathResponse = "/user/post/post_createduser_201.json";
        var pathRequest = "/user/post/post_createuser_200.json";

        var request = fileUtil.readFile(pathRequest);
        var expectedUserCreated = fileUtil.readFile(pathResponse);

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .isEqualTo(expectedUserCreated);

    }

    @ParameterizedTest
    @MethodSource("postParameterizedTest")
    @DisplayName("POST v1/users should return BAD REQUEST")
    @Order(10)
    void save_shouldReturnBadRequest_whenFailed(String fileName) {

        var request = fileUtil.readFile(fileName);
        var responseFile = fileUtil.readFile("/user/post/post_createuser-badrequest_400.json");

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();


        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .isEqualTo(responseFile);


    }

    private static Stream<Arguments> postParameterizedTest() {

        return Stream.of(
                Arguments.of("/user/post/post_createuser-empty-values_400.json"),
                Arguments.of("/user/post/post_createuser-blank-values_400.json")
        );
    }

    @ParameterizedTest
    @MethodSource("putParametrizedTest")
    @DisplayName("PUT v1/users payload with existentId should replace a user with valid email, firstName and Lastname")
    @Order(11)
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void replaceWithValidPayload_shouldReplaceUserWithValidPayload_whenSuccessfull(User user) {

        var existentUser = userRepository.findUserByFirstName("Johny");
        Assertions.assertThat(existentUser).isNotEmpty();
        user.setId(existentUser.get().getId());

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(user)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();
    }

    private static Stream<Arguments> putParametrizedTest() {
        var withNewEmail = User.builder().email("jorjin@gmail.com").build();
        var withNewFirstName = User.builder().firstName("Joaquin").build();
        var withNewLastName = User.builder().lastName("Bravo").build();

        return Stream.of(
                Arguments.of(withNewEmail),
                Arguments.of(withNewFirstName),
                Arguments.of(withNewLastName)
        );
    }

    @ParameterizedTest
    @MethodSource("putWithInvalidFields")
    @DisplayName("PUT v1/users payload should return BAD REQUEST with invalid fields")
    @Order(12)
    void replaceWithInvalidPayload_shouldReturnBadRequest_whenFailed(String fileName) {
        var request = fileUtil.readFile(fileName);

        var responseFile = fileUtil.readFile("/user/put/put_replacebadrequest_400.json");

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .isEqualTo(responseFile);

    }

    private static Stream<Arguments> putWithInvalidFields() {
        var invalidEmail = "/user/put/put_replacewithinvalidemail_400.json";
        var blankEmail = "/user/put/put_replacewithblank-empty-email_400.json";
        var blankFirstName = "/user/put/put_replacewithblank-empty-firstname_400.json";
        var blankLastName = "/user/put/put_replacewithblank-empty-lastname_400.json";

        return Stream.of(
                Arguments.of(invalidEmail),
                Arguments.of(blankEmail),
                Arguments.of(blankFirstName),
                Arguments.of(blankLastName)
        );
    }

    @Test
    @DisplayName("PUT v1/users payload with inexistentId should return NOT FOUND")
    @Order(13)
    void replace_shouldReturnNotFound_whenFailed() {
        var pathRequest = "/user/put/put_replacewithinexistentid_404.json";

        var pathResponse = "/user/put/put_replacenotfound_404.json";

        var request = fileUtil.readFile(pathRequest);

        var response = fileUtil.readFile(pathResponse);

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();

    }

    @Test
    @DisplayName("DELETE v1/users/{existentId} should delete user")
    @Order(14)
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteById_shouldDeleteUser_whenSuccessfull() {

        var existentUserId = userRepository.findUserByFirstName("Johny").get().getId();


        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", existentUserId)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

    }

    @Test
    @DisplayName("DELETE v1/users/{inexistentId} should return NOT FOUND")
    @Order(15)
    void deleteById_shouldReturnNotFound_whenFailed() {

        var pathResponse = "/user/delete/delete_idnotfound_404.json";

        var response = fileUtil.readFile(pathResponse);

        var inexistentId = 50L;

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", inexistentId)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();

    }
}
