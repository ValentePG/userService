package dev.valente.user_service.common;

import dev.valente.user_service.brasilapiclient.response.CepGetErrorResponse;
import dev.valente.user_service.brasilapiclient.response.CepGetResponse;
import dev.valente.user_service.brasilapiclient.response.CepInnerErrorResponse;
import dev.valente.user_service.domain.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CepDataUtil {
    public CepGetResponse newCepGetResponse() {
        return CepGetResponse.builder()
                .cep("00000")
                .city("São Paulo")
                .neighborhood("Vila Mariana")
                .street("Rua 123")
                .service("viacep")
                .build();
    }

    public CepGetErrorResponse newCepGetErrorResponse() {

        var cepInnerErrorResponse = CepInnerErrorResponse.builder()
                .name("null")
                .message("CEP informado possui mais do que 8 caracteres.")
                .service("cep_validation")
                .build();

        return CepGetErrorResponse.builder()
                .name("CepPromiseError")
                .message("CEP deve conter exatamente 8 caracteres.")
                .type("validation_error")
                .errors(List.of(cepInnerErrorResponse))
                .build();
    }

}
