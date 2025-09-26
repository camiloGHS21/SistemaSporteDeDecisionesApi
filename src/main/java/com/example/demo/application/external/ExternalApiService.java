package com.example.demo.application.external;

import reactor.core.publisher.Mono;



public interface ExternalApiService {

    Mono<java.util.List<com.example.demo.domain.external.ExternalData>> getExternalData();
}