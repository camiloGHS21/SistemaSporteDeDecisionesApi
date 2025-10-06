package com.example.demo.application.external;

import com.example.demo.domain.external.OecdData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

class OecdApiServiceImplTest {

    private MockWebServer mockWebServer;
    private OecdApiServiceImpl oecdApiService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        oecdApiService = new OecdApiServiceImpl(webClient, validator, objectMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getOecdData_whenApiReturnsValidData_thenReturnsOecdDataList() {
        String jsonResponse = "[ [ { \"page\": 1, \"pages\": 1, \"per_page\": \"50\", \"total\": 1 } ], [ { \"indicator\": { \"id\": \"NY.GDP.MKTP.CD\", \"value\": \"GDP (current US$)\" }, \"country\": { \"id\": \"MX\", \"value\": \"Mexico\" }, \"countryiso3code\": \"MEX\", \"date\": \"2020\", \"value\": 1103521085510.94, \"unit\": \"\", \"obs_status\": \"\", \"decimal\": 0 } ] ]";
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        Mono<List<OecdData>> result = oecdApiService.getOecdData("MX", "2020");

        StepVerifier.create(result)
                .expectNextMatches(list -> !list.isEmpty() && list.get(0).getName().equals("GDP (current US$)"))
                .verifyComplete();
    }

    @Test
    void getOecdData_whenApiReturnsInvalidData_thenReturnsEmptyList() {
        String jsonResponse = "{\"invalid\": \"data\"}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        Mono<List<OecdData>> result = oecdApiService.getOecdData("MX", "2020");

        StepVerifier.create(result)
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }
}
