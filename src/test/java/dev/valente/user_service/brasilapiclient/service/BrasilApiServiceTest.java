package dev.valente.user_service.brasilapiclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.valente.user_service.brasilapiclient.response.CepInnerErrorResponse;
import dev.valente.user_service.common.CepDataUtil;
import dev.valente.user_service.config.BrasilApiConfigurationProperties;
import dev.valente.user_service.config.RestClientConfiguration;
import dev.valente.user_service.exception.NotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;


@RestClientTest({BrasilApiService.class,
                RestClientConfiguration.class,
                BrasilApiConfigurationProperties.class,
                ObjectMapper.class,
                CepDataUtil.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BrasilApiServiceTest {

    @Autowired
    private BrasilApiService brasilApiService;

    @Autowired
    @Qualifier("brasilApiClient")
    private RestClient.Builder brasilApiClientBuilder;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private BrasilApiConfigurationProperties properties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CepDataUtil cepDataUtil;

    @AfterEach
    void reset() {
        server.reset();
    }

    @Order(1)
    @Test
    @DisplayName("findCep returns CepGetResponse when successful")
    void findCep_ReturnsCepGetResponse_WhenSuccessful() throws JsonProcessingException {
        server = MockRestServiceServer.bindTo(brasilApiClientBuilder).build();

        var cep = "00000000";
        var cepGetResponse = cepDataUtil.newCepGetResponse();
        var jsonResponse = objectMapper.writeValueAsString(cepGetResponse);

        var requestTo = MockRestRequestMatchers
                .requestToUriTemplate(properties.baseUrl()
                        + properties.cepUri(), cep);

        var withSuccess = MockRestResponseCreators.withSuccess(jsonResponse, MediaType.APPLICATION_JSON);

        server.expect(requestTo).andRespond(withSuccess);

        Assertions.assertThat(brasilApiService.findCep(cep))
                .isNotNull()
                .isEqualTo(cepGetResponse);

    }

    @Order(2)
    @Test
    @DisplayName("findCep returns CepInnerErrorResponse when Failed")
    void findCep_ReturnsCepInnerErrorResponse_WhenUnsucessfull() throws JsonProcessingException {
        server = MockRestServiceServer.bindTo(brasilApiClientBuilder).build();

        var cep = "4040000";
        var cepErrorResponse = cepDataUtil.newCepGetErrorResponse();
        var jsonResponse = objectMapper.writeValueAsString(cepErrorResponse);
        var expectedErrorMessage = """
                404 NOT_FOUND "CepGetErrorResponse[name=CepPromiseError, message=CEP deve conter exatamente 8 caracteres., type=validation_error, errors=[CepInnerErrorResponse[name=null, message=CEP informado possui mais do que 8 caracteres., service=cep_validation]]]"
                """.trim();
        var requestTo = MockRestRequestMatchers
                .requestToUriTemplate(properties.baseUrl()
                        + properties.cepUri(), cep);

        var withUnsuccess = MockRestResponseCreators.withResourceNotFound().body(jsonResponse);

        server.expect(requestTo).andRespond(withUnsuccess);

        Assertions.assertThatException()
                .isThrownBy(() -> brasilApiService.findCep(cep))
                .withMessage(expectedErrorMessage)
                .isInstanceOf(NotFoundException.class);

    }
}