package com.example.demo.application;

import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.domain.core.DatoIndicadorService;
import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisRepository;
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
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.application.file.CsvFileProcessingServiceImpl;
import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.validation.ValidationServiceImpl;
import com.example.demo.domain.file.DuplicateFileContentException;
import com.example.demo.domain.file.FileAlreadyExistsException;
import com.example.demo.domain.file.FileData;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CsvFileProcessingServiceTest {

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
    }

    @Test
    void testProcessFileWithValidData() {
        // Given
        String csvContent = "pais,tipo_indicador,valor,anio,fuente\nMexico,IndicadorA,123.45,2023,FuenteA";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        when(fileDataRepository.existsByFileName(any())).thenReturn(false);
        when(fileDataRepository.existsByFileHash(any())).thenReturn(false);
        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));
        when(datoIndicadorService.saveDatosIndicador(any(), any())).thenReturn(Collections.emptyList());
        when(fileDataRepository.save(any())).thenReturn(new FileData());

        // When
        List<String> errors = csvFileProcessingService.processFile(file);

        // Then
        assertTrue(errors.isEmpty());
        verify(fileDataRepository).save(any());
    }

    @Test
    void testProcessFileWithInvalidData() {
        // Given
        String csvContent = "pais,tipo_indicador,valor,anio,fuente\nMexico,IndicadorB,not-a-float,2023,FuenteB"; // Invalid float value
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        when(fileDataRepository.existsByFileName(any())).thenReturn(false);
        when(fileDataRepository.existsByFileHash(any())).thenReturn(false);

        // When
        List<String> errors = csvFileProcessingService.processFile(file);

        // Then
        assertTrue(errors.isEmpty());
        verify(fileDataRepository, never()).save(any());
    }

    @Test
    void testProcessFileWithMissingColumns() {
        // Given
        String csvContent = "pais,tipo_indicador,valor,anio,fuente\nMexico,IndicadorC,123.45"; // Missing columns
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        when(fileDataRepository.existsByFileName(any())).thenReturn(false);
        when(fileDataRepository.existsByFileHash(any())).thenReturn(false);

        // When
        List<String> errors = csvFileProcessingService.processFile(file);

        // Then
        assertTrue(errors.isEmpty());
        verify(fileDataRepository, never()).save(any());
    }

    @Test
    void testProcessFileWithExistingFileName() {
        // Given
        String csvContent = "pais,tipo_indicador,valor,anio,fuente\nMexico,IndicadorA,123.45,2023,FuenteA";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        when(fileDataRepository.existsByFileName(any())).thenReturn(true);

        // When & Then
        assertThrows(FileAlreadyExistsException.class, () -> {
            csvFileProcessingService.processFile(file);
        });
    }

    @Test
    void testProcessFileWithExistingFileHash() {
        // Given
        String csvContent = "pais,tipo_indicador,valor,anio,fuente\nMexico,IndicadorA,123.45,2023,FuenteA";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        when(fileDataRepository.existsByFileName(any())).thenReturn(false);
        when(fileDataRepository.existsByFileHash(any())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateFileContentException.class, () -> {
            csvFileProcessingService.processFile(file);
        });
    }
}
