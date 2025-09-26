package com.example.demo.application.external;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.domain.external.OecdData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import reactor.core.publisher.Mono;


@Service
public class OecdApiServiceImpl implements OecdApiService {

    private final WebClient worldBankWebClient;
   
    private final Validator validator;
  
    private final ObjectMapper objectMapper;

  
    public OecdApiServiceImpl(@Qualifier("worldBankWebClient") WebClient worldBankWebClient, Validator validator, ObjectMapper objectMapper) {
        this.worldBankWebClient = worldBankWebClient;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }


    @Override
    public Mono<List<OecdData>> getOecdData(String countryCode, String year) {
        return worldBankWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/country/{countryCode}/indicator/NY.GDP.MKTP.CD")
                        .queryParam("date", year)
                        .queryParam("format", "json")
                        .build(countryCode))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseAndValidateOecdResponse);
    }

 
    private List<OecdData> parseAndValidateOecdResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            if (rootNode.isArray() && rootNode.size() > 1) {
                JsonNode dataNode = rootNode.get(1);
                if (dataNode.isArray()) {
                    List<OecdData> oecdDataList = new ArrayList<>();
                    for (JsonNode itemNode : (ArrayNode) dataNode) {
                        String id = itemNode.path("indicator").path("id").asText(null);
                        String name = itemNode.path("indicator").path("value").asText(null);
                        JsonNode valueNode = itemNode.path("value");
                        String dataValue = valueNode.isNull() ? null : valueNode.asText();

                        if (id != null && name != null && dataValue != null) {
                            oecdDataList.add(new OecdData(id, name, dataValue));
                        }
                    }

                    return oecdDataList.stream()
                            .filter(data -> {
                                Set<ConstraintViolation<OecdData>> violations = validator.validate(data);
                                if (violations.isEmpty()) {
                                    return true;
                                } else {
                                    // En una aplicación real, esto debería ser manejado por un sistema de logging.
                                    System.out.println("Errores de validación para datos de la OCDE: " + data);
                                    for (ConstraintViolation<OecdData> violation : violations) {
                                        System.out.println(violation.getMessage());
                                    }
                                    return false;
                                }
                            })
                            .collect(Collectors.toList());
                }
            }
        } catch (JsonProcessingException e) {
            // En una aplicación real, esto debería ser manejado por un sistema de logging.
            System.err.println("Error al parsear la respuesta JSON: " + e.getMessage());
        }
        return Collections.emptyList(); // Devuelve una lista vacía en caso de error.
    }
}
