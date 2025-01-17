package dev.valente.user_service.profile.controller;

import dev.valente.user_service.common.FileUtil;
import dev.valente.user_service.common.ProfileDataUtil;
import dev.valente.user_service.config.IntegrationTestConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.util.stream.Stream;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileControllerRestAssuredIT extends IntegrationTestConfig {

    private static final String URL = "/v1/profiles";

    @Autowired
    private ProfileDataUtil profileDataUtil;

    @Autowired
    private FileUtil fileUtil;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUrl() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("GET v1/profiles returns a list with all profiles")
    @Order(1)
    @Sql(value = "/sql/init_two_profiles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_profiles.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_ReturnsAllProfiles_WhenSuccessfull() {
        var response = fileUtil.readFile("/profile/get/get_findall_200.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();

    }

    @Test
    @DisplayName("GET v1/profiles returns a list empty")
    @Order(2)
    void findAll_ReturnsEmptyList_WhenNothingIsFound() {

        var response = fileUtil.readFile("/profile/get/get_findall-empty_200.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();


    }

    @Test
    @DisplayName("POST v1/profiles creates an profile")
    @Order(3)
    void createProfile_shouldSaveUser_WhenSuccessfull() throws IOException {
        var request = fileUtil.readFile("/profile/post/post_createprofilewithvaliddata_200.json");
        var expectedResponse = fileUtil.readFile("/profile/post/post_createdprofile_201.json");

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
                .node("id")
                .asNumber()
                .isPositive();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id")
                .when(Option.IGNORING_ARRAY_ORDER)
                .isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @MethodSource("postParameterizedTest")
    @DisplayName("POST v1/profiles should return BAD REQUEST")
    @Order(4)
    void createProfile_shouldReturnBadRequest_withInvalidData(String requestFile, String responseFile) throws Exception {
        var request = fileUtil.readFile(requestFile);
        var expectedResponse = fileUtil.readFile(responseFile);


        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        // Importante
        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("timestamp")
                .isEqualTo(expectedResponse);


    }

    private static Stream<Arguments> postParameterizedTest() throws IOException {
        var requestBlankData = "/profile/post/post_createprofilewithdata-blank_400.json";
        var requestEmptyData = "/profile/post/post_createprofilewithdata-empty_400.json";
        var requestNullData = "/profile/post/post_createprofilewithdata-null_400.json";

        var responseBlankData = "/profile/post/post_response_createprofilewithdata-blank_400.json";
        var responseEmptyData = "/profile/post/post_response_createprofilewithdata-empty_400.json";
        var responseNullData = "/profile/post/post_response_createprofilewithdata-null_400.json";

        return Stream.of(
                Arguments.of(requestBlankData, responseBlankData),
                Arguments.of(requestEmptyData, responseEmptyData),
                Arguments.of(requestNullData, responseNullData)
        );
    }


}
