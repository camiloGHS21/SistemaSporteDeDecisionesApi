package com.example.demo.application;

import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisRepository;
import com.example.demo.domain.report.Informe;
import com.example.demo.domain.report.InformePaisComparacionRepository;
import com.example.demo.domain.report.InformeRepository;
import com.example.demo.domain.user.User;
import com.example.demo.infrastructure.report.ReportRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.application.report.ReportServiceImpl;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private DatoIndicadorRepository datoIndicadorRepository;

    @Mock
    private InformeRepository informeRepository;

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private InformePaisComparacionRepository informePaisComparacionRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    void testGenerateReport() {
        // Given
        User user = new User();
        ReportRequest request = new ReportRequest();
        request.setReportName("Test Report");
        request.setReportType("PDF");
        request.setPaises(Collections.singletonList("Mexico"));
        request.setIndicadores(Collections.singletonList("IndicadorA"));

        when(paisRepository.findByNombrePais(any())).thenReturn(Optional.of(new Pais()));
        when(informeRepository.save(any(Informe.class))).thenReturn(new Informe());

        // When
        byte[] pdfBytes = reportService.generateReport(request, user);

        // Then
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        // A simple check for PDF header
        assertTrue(new String(pdfBytes, 0, 5).startsWith("%PDF-"));
    }
}
