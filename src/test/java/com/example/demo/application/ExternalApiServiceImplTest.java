package com.example.demo.application;

import com.example.demo.domain.external.ExternalData;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.example.demo.application.external.ExternalApiService;
import com.example.demo.application.external.ExternalApiServiceImpl;

class ExternalApiServiceImplTest {

    private static MockWebServer mockWebServer;
    private ExternalApiService externalApiService;

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
        externalApiService = new ExternalApiServiceImpl(webClient, validator);
    }

    @Test
    void getExternalData() {
        String csvResponse = "Country,Year,Indicator,Value\n" +
                             "USA,2020,Internet Users,100\n" +
                             "Canada,2020,Internet Users,50";

        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "text/csv")
                .setBody(csvResponse);

        mockWebServer.enqueue(mockResponse);

        Mono<List<ExternalData>> externalDataListMono = externalApiService.getExternalData();

        StepVerifier.create(externalDataListMono)
                .expectNextMatches(dataList -> {
                    assertEquals(2, dataList.size());
                    assertEquals("USA", dataList.get(0).getCountry());
                    assertEquals("2020", dataList.get(0).getDataYear());
                    assertEquals("Internet Users", dataList.get(0).getIndicator());
                    assertEquals("100", dataList.get(0).getDataValue());
                    assertEquals("Canada", dataList.get(1).getCountry());
                    assertEquals("2020", dataList.get(1).getDataYear());
                    assertEquals("Internet Users", dataList.get(1).getIndicator());
                    assertEquals("50", dataList.get(1).getDataValue());
                    return true;
                })
                .verifyComplete();
    }
}
