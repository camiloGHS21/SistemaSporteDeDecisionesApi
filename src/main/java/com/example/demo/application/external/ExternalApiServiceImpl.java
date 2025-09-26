package com.example.demo.application.external;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.domain.external.ExternalData;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import reactor.core.publisher.Mono;


@Service
public class ExternalApiServiceImpl implements ExternalApiService {

    
    private final WebClient webClient;
   
    private final Validator validator;


    public ExternalApiServiceImpl(@Qualifier("ituWebClient") WebClient webClient, Validator validator) {
        this.webClient = webClient;
        this.validator = validator;
    }

    @Override
    public Mono<List<ExternalData>> getExternalData() {
        return this.webClient.get().uri("/data/download/byid/8941/iscollection/false")
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseAndValidateCsv);
    }

 
    private List<ExternalData> parseAndValidateCsv(String csvData) {
        List<ExternalData> externalDataList = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new StringReader(csvData))) {
            List<String[]> records = reader.readAll();
            // Asume que la primera fila es la cabecera y la ignora.
            if (records.size() > 1) {
                for (int i = 1; i < records.size(); i++) {
                    String[] record = records.get(i);
                    // Asume el orden: Country, Year, Indicator, Value
                    if (record.length >= 4) {
                        ExternalData data = new ExternalData();
                        data.setCountry(record[0]);
                        data.setDataYear(record[1]);
                        data.setIndicator(record[2]);
                        data.setDataValue(record[3]);

                        Set<ConstraintViolation<ExternalData>> violations = validator.validate(data);
                        if (violations.isEmpty()) {
                            externalDataList.add(data);
                        } else {
                            // En una aplicación real, esto debería ser manejado por un sistema de logging.
                            System.out.println("Errores de validación para el registro: " + String.join(",", record));
                            for (ConstraintViolation<ExternalData> violation : violations) {
                                System.out.println(violation.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (IOException | CsvException e) {
            // En una aplicación real, esto debería ser manejado por un sistema de logging.
            e.printStackTrace();
        }
        return externalDataList;
    }
}