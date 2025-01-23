package dev.valente.user_service.brasilapiclient.controller;

import dev.valente.user_service.brasilapiclient.response.CepGetResponse;
import dev.valente.user_service.brasilapiclient.service.BrasilApiService;
import dev.valente.user_service.config.BrasilApiConfigurationProperties;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/brasil-api/cep")
@Log4j2
@SecurityRequirement(name = "basicAuth")
public class BrasilApiController {

    private final BrasilApiService service;

    @GetMapping("/{cep}")
    public ResponseEntity<CepGetResponse> getCsrfToken(@PathVariable String cep) {
        log.info("request received to find cep");
        var cepGetResponse = service.findCep(cep);
        return ResponseEntity.ok(cepGetResponse);
    }
}
