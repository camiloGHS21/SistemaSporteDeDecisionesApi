package com.example.demo.application;

import com.example.demo.domain.file.ValidatedDataRow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.validation.ValidationServiceImpl;

public class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationServiceImpl();
    }

    @Test
    void whenNamesAreUnique_thenNoErrors() {
        // Given
        List<ValidatedDataRow> rows = new ArrayList<>();
        rows.add(createRow("name1", 10));
        rows.add(createRow("name2", 20));

        // When
        List<String> errors = validationService.validateUniqueNames(rows);

        // Then
        assertTrue(errors.isEmpty());
    }

    @Test
    void whenNamesAreDuplicated_thenErrorIsReturned() {
        // Given
        List<ValidatedDataRow> rows = new ArrayList<>();
        rows.add(createRow("name1", 10));
        rows.add(createRow("name1", 20));

        // When
        List<String> errors = validationService.validateUniqueNames(rows);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("El nombre 'name1' está duplicado en el archivo."));
    }

    @Test
    void whenListIsEmpty_thenNoErrors() {
        // Given
        List<ValidatedDataRow> rows = new ArrayList<>();

        // When
        List<String> errors = validationService.validateUniqueNames(rows);

        // Then
        assertTrue(errors.isEmpty());
    }

    @Test
    void whenNameIsNull_thenItIsIgnored() {
        // Given
        List<ValidatedDataRow> rows = new ArrayList<>();
        rows.add(createRow(null, 10));
        rows.add(createRow(null, 20));

        // When
        List<String> errors = validationService.validateUniqueNames(rows);

        // Then
        assertTrue(errors.isEmpty());
    }

    private ValidatedDataRow createRow(String name, int value) {
        ValidatedDataRow row = new ValidatedDataRow();
        row.setName(name);
        row.setValue(value);
        return row;
    }
}
