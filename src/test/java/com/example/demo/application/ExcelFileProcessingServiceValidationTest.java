package com.example.demo.application;

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
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.application.file.ExcelFileProcessingServiceImpl;
import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.validation.ValidationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ExcelFileProcessingServiceValidationTest {

    @Mock
    private FileDataRepository fileDataRepository;

    private Validator validator;

        private ExcelFileProcessingServiceImpl excelFileProcessingService;
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validationService = new ValidationServiceImpl();
        excelFileProcessingService = new ExcelFileProcessingServiceImpl(fileDataRepository, validator, validationService);
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
        Object[][] data = {{"name", "value"}, {"", 10}};
        MockMultipartFile file = createExcelFile(data);

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
        Object[][] data = {{"name", "value"}, {longName, 10}};
        MockMultipartFile file = createExcelFile(data);

        // When
        List<String> errors = excelFileProcessingService.processFile(file);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("El nombre no puede tener más de 50 caracteres."));
    }

    @Test
    public void whenValueIsNegative_thenValidationErrorIsReturned() throws IOException {
        // Given
        Object[][] data = {{"name", "value"}, {"negative", -1}};
        MockMultipartFile file = createExcelFile(data);

        // When
        List<String> errors = excelFileProcessingService.processFile(file);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("El valor debe ser positivo."));
    }
}