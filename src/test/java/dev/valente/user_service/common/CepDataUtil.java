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
                .name("ServiceError")
                .message("CEP INVÁLIDO")
                .service("correios")
                .build();

        return CepGetErrorResponse.builder()
                .name("CepPromiseError")
                .message("Todos os serviços de CEP retornaram erro.")
                .type("service_error")
                .errors(List.of(cepInnerErrorResponse))
                .build();
    }

}
