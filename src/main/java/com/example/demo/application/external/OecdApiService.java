package com.example.demo.application.external;

import java.util.List;

import com.example.demo.domain.external.OecdData;

import reactor.core.publisher.Mono;



public interface OecdApiService {

    Mono<List<OecdData>> getOecdData(String countryCode, String year);
}
