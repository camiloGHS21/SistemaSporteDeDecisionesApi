package com.example.demo.application;

import com.example.demo.domain.file.FileDataRepository;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.demo.application.file.CsvFileProcessingServiceImpl;
import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.validation.ValidationServiceImpl;

@ExtendWith(MockitoExtension.class)
class CsvFileProcessingServiceTest {

    @Mock
    private FileDataRepository fileDataRepository;

    private Validator validator;

        private CsvFileProcessingServiceImpl csvFileProcessingService;
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validationService = new ValidationServiceImpl();
        csvFileProcessingService = new CsvFileProcessingServiceImpl(fileDataRepository, validator, validationService);
    }

    @Test
    void testProcessFileWithValidData() {
        // Given
        String csvContent = "tipo_indicador,valor,anio,fuente\nIndicadorA,123.45,2023,FuenteA";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        // When
        csvFileProcessingService.processFile(file);

        // Then
        verify(fileDataRepository).save(any());
    }

    @Test
    void testProcessFileWithInvalidData() {
        // Given
        String csvContent = "tipo_indicador,valor,anio,fuente\nIndicadorB,not-a-float,2023,FuenteB"; // Invalid float value
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        // When
        csvFileProcessingService.processFile(file);

        // Then
        // The row with invalid data should be skipped, but the file is still processed.
        // Depending on the exact requirements, we might want to save even with partial data.
        // For now, let's assume the file is saved if at least one row is valid.
        // In this case, no rows are valid, so we don't save.
        verify(fileDataRepository, never()).save(any());
    }

    @Test
    void testProcessFileWithMissingColumns() {
        // Given
        String csvContent = "tipo_indicador,valor,anio,fuente\nIndicadorC,123.45"; // Missing columns
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        // When
        csvFileProcessingService.processFile(file);

        // Then
        verify(fileDataRepository, never()).save(any());
    }
}
