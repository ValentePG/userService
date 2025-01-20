package dev.valente.user_service.profile.controller;

import dev.valente.user_service.common.FileUtil;
import dev.valente.user_service.common.ProfileDataUtil;
import dev.valente.user_service.config.IntegrationTestConfig;
import dev.valente.user_service.config.TestRestTemplateConfig;
import dev.valente.user_service.profile.dto.get.ProfileGetResponse;
import dev.valente.user_service.profile.dto.post.ProfilePostResponse;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestRestTemplateConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(value = "/sql/init_one_login_regular_user.sql")
@Sql(value = "/sql/drop_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class ProfileControllerIT extends IntegrationTestConfig {
    private static final String URL = "/v1/profiles";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProfileDataUtil profileDataUtil;

    @Autowired
    private FileUtil fileUtil;

    @Test
    @DisplayName("GET v1/profiles returns a list with all profiles")
    @Order(1)
    @Sql(value = "/sql/init_two_profiles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/drop_profiles.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_ReturnsAllProfiles_WhenSuccessfull() {
        var typeReference = new ParameterizedTypeReference<List<ProfileGetResponse>>() {
        };
        var responseEntity = testRestTemplate.exchange(URL,
                GET, null, typeReference);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotEmpty().doesNotContainNull();

        responseEntity
                .getBody()
                .forEach(p -> assertThat(p).hasNoNullFieldsOrProperties());
    }

    @Test
    @DisplayName("GET v1/profiles returns a list empty")
    @Order(2)
    void findAll_ReturnsEmptyList_WhenNothingIsFound() {
        var typeReference = new ParameterizedTypeReference<List<ProfileGetResponse>>() {
        };

        var responseEntity = testRestTemplate.exchange(URL,
                GET, null, typeReference);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull().isEmpty();

    }

    @Test
    @DisplayName("POST v1/profiles creates an profile")
    @Order(3)
    void createProfile_shouldSaveUser_WhenSuccessfull() throws IOException {
        var request = fileUtil.readFile("/profile/post/post_createprofilewithvaliddata_200.json");

        var profileEntity = buildHttpEntity(request);

        var responseEntity = testRestTemplate.exchange(URL,
                POST, profileEntity, ProfilePostResponse.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull().hasNoNullFieldsOrProperties();
    }

    @ParameterizedTest
    @MethodSource("postParameterizedTest")
    @DisplayName("POST v1/profiles should return BAD REQUEST")
    @Order(4)
    void createProfile_shouldReturnBadRequest_withInvalidData(String requestFile, String responseFile) throws Exception {
        var request = fileUtil.readFile(requestFile);
        var expectedResponse = fileUtil.readFile(responseFile);

        var profileEntity = buildHttpEntity(request);
        var responseEntity = testRestTemplate.exchange(URL, POST, profileEntity, String.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


        // Importante
        JsonAssertions.assertThatJson(responseEntity.getBody())
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

    private static HttpEntity<String> buildHttpEntity(String request) {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request, httpHeaders);
    }
}
