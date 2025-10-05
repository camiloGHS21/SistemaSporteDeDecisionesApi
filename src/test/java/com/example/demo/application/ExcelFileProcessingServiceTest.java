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
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.application.file.ExcelFileProcessingServiceImpl;
import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.validation.ValidationServiceImpl;
import com.example.demo.domain.file.DuplicateFileContentException;
import com.example.demo.domain.file.FileAlreadyExistsException;
import com.example.demo.domain.file.FileData;

@ExtendWith(MockitoExtension.class)
class ExcelFileProcessingServiceTest {

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
    }

    @Test
    void testProcessFileWithValidData() throws IOException {
        // Given
        byte[] excelContent = createExcelContent("Mexico", "IndicadorA", 123.45, 2023, "FuenteA");
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelContent);
        when(fileDataRepository.existsByFileName(any())).thenReturn(false);
        when(fileDataRepository.existsByFileHash(any())).thenReturn(false);
        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));
        when(datoIndicadorService.saveDatosIndicador(any(), any())).thenReturn(Collections.emptyList());
        when(fileDataRepository.save(any())).thenReturn(new FileData());

        // When
        List<String> errors = excelFileProcessingService.processFile(file);

        // Then
        assertTrue(errors.isEmpty());
        verify(fileDataRepository).save(any());
    }

    @Test
    void testProcessFileWithInvalidData() throws IOException {
        // Given
        byte[] excelContent = createExcelContent("Mexico", "", 123.45, 2023, "FuenteA"); // Invalid name (empty)
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelContent);
        when(fileDataRepository.existsByFileName(any())).thenReturn(false);
        when(fileDataRepository.existsByFileHash(any())).thenReturn(false);
        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));

        // When
        List<String> errors = excelFileProcessingService.processFile(file);

        // Then
        // We expect a validation error, so the file should not be saved.
        assertTrue(errors.size() > 0);
        verify(fileDataRepository, never()).save(any());
    }

    private byte[] createExcelContent(String pais, String tipoIndicador, double valor, int anio, String fuente) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Test Sheet");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("pais");
            headerRow.createCell(1).setCellValue("tipo_indicador");
            headerRow.createCell(2).setCellValue("valor");
            headerRow.createCell(3).setCellValue("anio");
            headerRow.createCell(4).setCellValue("fuente");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(pais);
            dataRow.createCell(1).setCellValue(tipoIndicador);
            dataRow.createCell(2).setCellValue(valor);
            dataRow.createCell(3).setCellValue(anio);
            dataRow.createCell(4).setCellValue(fuente);

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    @Test
    void testProcessFileWithExistingFileName() throws IOException {
        // Given
        byte[] excelContent = createExcelContent("Mexico", "IndicadorA", 123.45, 2023, "FuenteA");
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelContent);
        when(fileDataRepository.existsByFileName(any())).thenReturn(true);

        // When & Then
        assertThrows(FileAlreadyExistsException.class, () -> {
            excelFileProcessingService.processFile(file);
        });
    }

    @Test
    void testProcessFileWithExistingFileHash() throws IOException {
        // Given
        byte[] excelContent = createExcelContent("Mexico", "IndicadorA", 123.45, 2023, "FuenteA");
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelContent);
        when(fileDataRepository.existsByFileName(any())).thenReturn(false);
        when(fileDataRepository.existsByFileHash(any())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateFileContentException.class, () -> {
            excelFileProcessingService.processFile(file);
        });
    }
}
