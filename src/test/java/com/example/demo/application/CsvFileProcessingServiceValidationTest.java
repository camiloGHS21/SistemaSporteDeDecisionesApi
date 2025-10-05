package com.example.demo.application;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import com.example.demo.application.file.CsvFileProcessingServiceImpl;
import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.validation.ValidationServiceImpl;
import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.domain.core.DatoIndicadorService;
import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisRepository;
import com.example.demo.domain.file.FileDataRepository;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CsvFileProcessingServiceValidationTest {

    @Mock
    private FileDataRepository fileDataRepository;

    @Mock
    private DatoIndicadorRepository datoIndicadorRepository;

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private DatoIndicadorService datoIndicadorService;

    private Validator validator;

    private CsvFileProcessingServiceImpl csvFileProcessingService;
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validationService = new ValidationServiceImpl(datoIndicadorRepository, paisRepository);
        csvFileProcessingService = new CsvFileProcessingServiceImpl(fileDataRepository, validator, validationService, datoIndicadorService);
        when(fileDataRepository.existsByFileName(any())).thenReturn(false);
        when(fileDataRepository.existsByFileHash(any())).thenReturn(false);
        when(datoIndicadorService.saveDatosIndicador(any(), any())).thenReturn(Collections.emptyList());
    }

    @Test
    public void whenNameIsEmpty_thenValidationErrorIsReturned() throws IOException {
        // Given
        String csvContent = "pais,tipo_indicador,valor,anio,fuente\nMexico,,123.45,2023,FuenteA"; // Invalid row with empty name
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));

        // When
        List<String> errors = csvFileProcessingService.processFile(file);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("El nombre no puede estar vacío."));
    }

    @Test
    public void whenNameIsNull_thenValidationErrorIsReturned() throws IOException {
        // Given
        String csvContent = "pais,tipo_indicador,valor,anio,fuente\nMexico,,123.45,2023,FuenteA";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));

        // When
        List<String> errors = csvFileProcessingService.processFile(file);

        // Then
        // The CSV reader will read an empty string, so this is covered by the test above.
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("El nombre no puede estar vacío."));
    }

    @Test
    public void whenNameIsTooLong_thenValidationErrorIsReturned() throws IOException {
        // Given
        String longName = "a".repeat(51);
        String csvContent = "pais,tipo_indicador,valor,anio,fuente\nMexico," + longName + ",123.45,2023,FuenteA";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));

        // When
        List<String> errors = csvFileProcessingService.processFile(file);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("El nombre no puede tener más de 50 caracteres."));
    }

    @Test
    public void whenValueIsNegative_thenValidationErrorIsReturned() throws IOException {
        // Given
        String csvContent = "pais,tipo_indicador,valor,anio,fuente\nMexico,IndicadorA,-1,2023,FuenteA";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));

        // When
        List<String> errors = csvFileProcessingService.processFile(file);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("El valor debe ser positivo."));
    }

    @Test
    public void whenValueIsInvalidFloat_thenErrorIsReturned() throws IOException {
        // Given
        String csvContent = "pais,tipo_indicador,valor,anio,fuente\nMexico,IndicadorA,not-a-float,2023,FuenteA";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        // When
        csvFileProcessingService.processFile(file);

        // Then
        // The row is skipped, so no validation errors are returned, and the file is not saved.
        verify(fileDataRepository, never()).save(any());
    }
}
