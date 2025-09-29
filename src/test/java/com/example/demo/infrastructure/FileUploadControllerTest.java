package com.example.demo.infrastructure;

import com.example.demo.application.file.FileProcessingService;
import com.example.demo.application.Auth.JwtTokenProvider;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.infrastructure.file.FileUploadController;

@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "csvFileProcessingService")
    private FileProcessingService csvFileProcessingService;

    @MockBean(name = "excelFileProcessingService")
    private FileProcessingService excelFileProcessingService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser
    void testUploadCsvFile() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "col1,col2\nval1,val2".getBytes());
        when(csvFileProcessingService.processFile(any())).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(multipart("/api/upload").file(file).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testUploadCsvFileWithValidationErrors() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "col1,col2\n,val2".getBytes());
        when(csvFileProcessingService.processFile(any())).thenReturn(Collections.singletonList("El nombre no puede estar vacío."));

        // When & Then
        mockMvc.perform(multipart("/api/upload").file(file).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("El nombre no puede estar vacío."));
    }

    @Test
    @WithMockUser
    void testUploadExcelFile() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "test data".getBytes());
        when(excelFileProcessingService.processFile(any())).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(multipart("/api/upload").file(file).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testUploadUnsupportedFile() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test data".getBytes());

        // When & Then
        mockMvc.perform(multipart("/api/upload").file(file).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testUploadEmptyFile() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", new byte[0]);

        // When & Then
        mockMvc.perform(multipart("/api/upload").file(file).with(csrf()))
                .andExpect(status().isBadRequest());
    }
}

