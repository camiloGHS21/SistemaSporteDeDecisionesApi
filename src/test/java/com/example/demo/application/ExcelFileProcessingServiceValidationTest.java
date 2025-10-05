package com.example.demo.application;

import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.domain.core.DatoIndicadorService;
import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisRepository;
import com.example.demo.domain.file.FileDataRepository;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.application.file.ExcelFileProcessingServiceImpl;
import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.validation.ValidationServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ExcelFileProcessingServiceValidationTest {

    @Mock
    private FileDataRepository fileDataRepository;

    @Mock
    private DatoIndicadorRepository datoIndicadorRepository;

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private DatoIndicadorService datoIndicadorService;

    private Validator validator;

    private ExcelFileProcessingServiceImpl excelFileProcessingService;
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validationService = new ValidationServiceImpl(datoIndicadorRepository, paisRepository);
        excelFileProcessingService = new ExcelFileProcessingServiceImpl(fileDataRepository, validator, validationService, datoIndicadorService);
        when(fileDataRepository.existsByFileName(any())).thenReturn(false);
        when(fileDataRepository.existsByFileHash(any())).thenReturn(false);
        when(datoIndicadorService.saveDatosIndicador(any(), any())).thenReturn(Collections.emptyList());
    }

    private MockMultipartFile createExcelFile(Object[][] data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < data[i].length; j++) {
                    if (data[i][j] instanceof String) {
                        row.createCell(j).setCellValue((String) data[i][j]);
                    } else if (data[i][j] instanceof Number) {
                        row.createCell(j).setCellValue(((Number) data[i][j]).doubleValue());
                    }
                }
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bos.toByteArray());
        }
    }

    @Test
    public void whenNameIsEmpty_thenValidationErrorIsReturned() throws IOException {
        // Given
        Object[][] data = {{"pais", "tipo_indicador", "valor", "anio", "fuente"}, {"Mexico", "", 123.45, 2023, "FuenteA"}};
        MockMultipartFile file = createExcelFile(data);
        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));

        // When
        List<String> errors = excelFileProcessingService.processFile(file);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("El nombre no puede estar vacío."));
    }

    @Test
    public void whenNameIsTooLong_thenValidationErrorIsReturned() throws IOException {
        // Given
        String longName = "a".repeat(51);
        Object[][] data = {{"pais", "tipo_indicador", "valor", "anio", "fuente"}, {"Mexico", longName, 123.45, 2023, "FuenteA"}};
        MockMultipartFile file = createExcelFile(data);
        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));

        // When
        List<String> errors = excelFileProcessingService.processFile(file);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("El nombre no puede tener más de 50 caracteres."));
    }

    @Test
    public void whenValueIsNegative_thenValidationErrorIsReturned() throws IOException {
        // Given
        Object[][] data = {{"pais", "tipo_indicador", "valor", "anio", "fuente"}, {"Mexico", "IndicadorA", -1, 2023, "FuenteA"}};
        MockMultipartFile file = createExcelFile(data);
        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));

        // When
        List<String> errors = excelFileProcessingService.processFile(file);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("El valor debe ser positivo."));
    }
}