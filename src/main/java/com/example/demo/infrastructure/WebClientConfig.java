package com.example.demo.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


    @Bean
    public WebClient ituWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://api.datahub.itu.int/v2/").build();
    }


    @Bean
    public WebClient worldBankWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://api.worldbank.org/v2/").build();
    }
}
