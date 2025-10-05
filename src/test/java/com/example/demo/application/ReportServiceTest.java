package com.example.demo.application;

import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.domain.file.FileData;
import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.infrastructure.report.ReportRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.application.report.ReportServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private FileDataRepository fileDataRepository;

    @Mock
    private DatoIndicadorRepository datoIndicadorRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void testGenerateReport() {
        // Given
        ReportRequest request = new ReportRequest();
        request.setReportName("Test Report");
        request.setReportType("PDF");
        request.setFileIds(Collections.singletonList(1L));

        // When
        byte[] pdfBytes = reportService.generateReport(request);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        // A simple check for PDF header
        assertTrue(new String(pdfBytes, 0, 5).startsWith("%PDF-"));
    }
}
