package dev.valente.user_service.user.controller;

import dev.valente.user_service.common.FileUtil;
import dev.valente.user_service.common.UserDataUtil;
import dev.valente.user_service.config.IntegrationTestConfig;
import dev.valente.user_service.config.RestAssuredConfig;
import dev.valente.user_service.user.repository.UserRepositoryJPA;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RestAssuredConfig.class)
public class UserControllerRestAssuredTestIT extends IntegrationTestConfig {

    private final String URL = "/v1/users";

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private UserDataUtil userDataUtil;

    @Autowired
    @Qualifier(value = "requestSpecificationAdmin")
    private RequestSpecification requestSpecificationAdminUser;

    @Autowired
    @Qualifier(value = "requestSpecificationRegularUser")
    private RequestSpecification requestSpecificationRegularUser;

    @Autowired
    private UserRepositoryJPA userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.requestSpecification = requestSpecificationRegularUser;
    }

    @Test
    @DisplayName("GET v1/users should return list of all users")
    @Order(1)
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_shouldReturnListOfUsers_whenSuccessfull() {
        RestAssured.requestSpecification = requestSpecificationAdminUser;

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
    @DisplayName("GET v1/users/{existentId} should return a user")
    @Order(2)
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
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(3)
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
    @Order(4)
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
    @Order(5)
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    @Order(6)
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
    @Order(7)
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    @Order(8)
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
    @Order(9)
    void save_shouldReturnBadRequest_whenFailed(String fileName, String fileError) {

        var request = fileUtil.readFile(fileName);
        var responseFile = fileUtil.readFile(fileError);

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
                .withOptions(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(responseFile);

    }

    private static Stream<Arguments> postParameterizedTest() {


        return Stream.of(
                Arguments.of("/user/post/post_createuser-empty-values_400.json",
                        "/user/post/post_response-empty_400.json"),
                Arguments.of("/user/post/post_createuser-blank-values_400.json",
                        "/user/post/post_response-blank_400.json")
        );
    }

//    @ParameterizedTest
//    @MethodSource("putParametrizedTest")
//    @DisplayName("PUT v1/users payload with existentId should replace a user with valid email, firstName and Lastname")
//    @Order(10)
//    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    void replaceWithValidPayload_shouldReplaceUserWithValidPayload_whenSuccessfull(User user) {
//
//        var existentUser = userRepository.findUserByFirstName("Johny");
//        Assertions.assertThat(existentUser).isNotEmpty();
//        user.setId(existentUser.get().getId());
//
//        RestAssured.given()
//                .contentType(ContentType.JSON).accept(ContentType.JSON)
//                .body(user)
//                .when()
//                .put(URL)
//                .then()
//                .statusCode(HttpStatus.NO_CONTENT.value())
//                .log().all();
//    }
//
//    private static Stream<Arguments> putParametrizedTest() {
//        var withNewEmail = User.builder().email("jorjin@gmail.com").build();
//        var withNewFirstName = User.builder().firstName("Joaquin").build();
//        var withNewLastName = User.builder().lastName("Bravo").build();
//
//        return Stream.of(
//                Arguments.of(withNewEmail),
//                Arguments.of(withNewFirstName),
//                Arguments.of(withNewLastName)
//        );
//    }

//    @ParameterizedTest
//    @MethodSource("putWithInvalidFields")
//    @DisplayName("PUT v1/users payload should return BAD REQUEST with invalid fields")
//    @Order(12)
//    void replaceWithInvalidPayload_shouldReturnBadRequest_whenFailed(String fileName, String fileError) {
//        var request = fileUtil.readFile(fileName);
//
//        var responseFile = fileUtil.readFile(fileError);
//
//        var response = RestAssured.given()
//                .contentType(ContentType.JSON).accept(ContentType.JSON)
//                .body(request)
//                .when()
//                .put(URL)
//                .then()
//                .statusCode(HttpStatus.BAD_REQUEST.value())
//                .log().all()
//                .extract().response().body().asString();
//
//        JsonAssertions.assertThatJson(response)
//                .whenIgnoringPaths("timestamp")
//                .isEqualTo(responseFile);
//
//    }
//
//    private static Stream<Arguments> putWithInvalidFields() {
//        var invalidEmail = "/user/put/put_replacewithinvalidemail_400.json";
//        var blankEmail = "/user/put/put_replacewithblank-empty-email_400.json";
//        var blankFirstName = "/user/put/put_replacewithblank-empty-firstname_400.json";
//        var blankLastName = "/user/put/put_replacewithblank-empty-lastname_400.json";
//
//        var invalidEmailError = "/user/put/put_response-replacewithinvalidemail_400.json";
//        var blankEmailError = "/user/put/put_response-replace-empty-email_400.json";
//        var blankFirstNameError = "/user/put/put_response-replace-empty-firstname_400.json";
//        var blankLastNameError = "/user/put/put_response-replace-empty-lastname_400.json";
//
//        return Stream.of(
//                Arguments.of(invalidEmail, invalidEmailError),
//                Arguments.of(blankEmail, blankEmailError),
//                Arguments.of(blankFirstName, blankFirstNameError),
//                Arguments.of(blankLastName, blankLastNameError)
//        );
//    }

//    @Test
//    @DisplayName("PUT v1/users payload with inexistentId should return NOT FOUND")
//    @Order(11)
//    void replace_shouldReturnNotFound_whenFailed() {
//        var pathRequest = "/user/put/put_replacewithinexistentid_404.json";
//
//        var pathResponse = "/user/put/put_replacenotfound_404.json";
//
//        var request = fileUtil.readFile(pathRequest);
//
//        var response = fileUtil.readFile(pathResponse);
//
//        RestAssured.given()
//                .contentType(ContentType.JSON).accept(ContentType.JSON)
//                .body(request)
//                .when()
//                .put(URL)
//                .then()
//                .statusCode(HttpStatus.NOT_FOUND.value())
//                .body(Matchers.equalTo(response))
//                .log().all();
//
//    }

    @Test
    @DisplayName("DELETE v1/users/{existentId} should delete user")
    @Order(12)
    @Sql(value = "/sql/init_sql_threeusers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteById_shouldDeleteUser_whenSuccessfull() {
        RestAssured.requestSpecification = requestSpecificationAdminUser;

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
    @Order(13)
    @Sql(value = "/sql/init_one_login_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteById_shouldReturnNotFound_whenFailed() {
        RestAssured.requestSpecification = requestSpecificationAdminUser;

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
