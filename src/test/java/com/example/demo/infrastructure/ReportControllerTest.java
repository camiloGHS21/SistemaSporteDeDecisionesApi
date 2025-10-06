package com.example.demo.infrastructure;

import com.example.demo.application.Auth.JwtTokenProvider;
import com.example.demo.application.report.ReportService;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.infrastructure.report.ReportController;
import com.example.demo.infrastructure.report.ReportRequest;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportService reportService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username = "testuser@example.com")
    void testGenerateReport() throws Exception {
        // Given
        ReportRequest request = new ReportRequest();
        request.setReportName("Test Report");
        request.setReportType("PDF");
        request.setPaises(Collections.singletonList("Mexico"));
        request.setIndicadores(Collections.singletonList("IndicadorA"));

        User mockUser = new User();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
        when(reportService.generateReport(any(ReportRequest.class), any(User.class))).thenReturn(new byte[10]);

        // When & Then
        mockMvc.perform(post("/api/reports")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
