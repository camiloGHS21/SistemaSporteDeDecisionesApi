package com.example.demo.application;

import com.example.demo.domain.external.OecdData;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Validator;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.application.external.OecdApiService;
import com.example.demo.application.external.OecdApiServiceImpl;

class OecdApiServiceImplTest {

    private static MockWebServer mockWebServer;
    private OecdApiService oecdApiService;
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        Validator validator = mock(Validator.class);
        when(validator.validate(any(OecdData.class))).thenReturn(Collections.emptySet());
        objectMapper = new ObjectMapper();
        oecdApiService = new OecdApiServiceImpl(webClient, validator, objectMapper);
    }

    @Test
    void getOecdData() {
        String jsonResponse = """
            [
              { "page": 1, "pages": 1 },
              [
                {
                  "indicator": { "id": "NY.GDP.MKTP.CD", "value": "GDP (current US$)" },
                  "country": { "id": "US", "value": "United States" },
                  "value": "21433226000000",
                  "date": "2020"
                }
              ]
            ]
        """;

        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(jsonResponse);

        mockWebServer.enqueue(mockResponse);

        Mono<List<OecdData>> oecdDataListMono = oecdApiService.getOecdData("usa", "2020");

        StepVerifier.create(oecdDataListMono)
                .expectNextMatches(dataList -> {
                    assertEquals(1, dataList.size());
                    assertEquals("NY.GDP.MKTP.CD", dataList.get(0).getId());
                    assertEquals("GDP (current US$)", dataList.get(0).getName());
                    assertEquals("21433226000000", dataList.get(0).getDataValue());
                    return true;
                })
                .verifyComplete();
    }
}
