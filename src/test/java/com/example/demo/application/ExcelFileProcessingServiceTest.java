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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.demo.application.file.ExcelFileProcessingServiceImpl;
import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.validation.ValidationServiceImpl;

@ExtendWith(MockitoExtension.class)
class ExcelFileProcessingServiceTest {

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

    @Test
    void testProcessFileWithValidData() throws IOException {
        // Given
        byte[] excelContent = createExcelContent("validName", 10);
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelContent);

        // When
        excelFileProcessingService.processFile(file);

        // Then
        verify(fileDataRepository).save(any());
    }

    @Test
    void testProcessFileWithInvalidData() throws IOException {
        // Given
        byte[] excelContent = createExcelContent("", 10); // Invalid name (empty)
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelContent);

        // When
        excelFileProcessingService.processFile(file);

        // Then
                // We expect a validation error, so the file should not be saved.
        verify(fileDataRepository, never()).save(any());
    }

    private byte[] createExcelContent(String name, int value) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Test Sheet");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Value");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(name);
            dataRow.createCell(1).setCellValue(value);

            workbook.write(baos);
            return baos.toByteArray();
        }
    }
}
