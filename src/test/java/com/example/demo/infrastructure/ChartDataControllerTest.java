package com.example.demo.infrastructure;

import com.example.demo.application.chart.ChartDataService;
import com.example.demo.application.Auth.JwtTokenProvider;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.infrastructure.chart.ChartDataController;
import com.example.demo.infrastructure.chart.ChartDataResponse;

@WebMvcTest(ChartDataController.class)
class ChartDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChartDataService chartDataService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser
    void testGetChartData() throws Exception {
        // Given
        ChartDataResponse response = new ChartDataResponse();
        response.setLabels(Collections.singletonList("Test Label"));
        ChartDataResponse.Dataset dataset = new ChartDataResponse.Dataset();
        dataset.setLabel("Test Dataset");
        dataset.setData(Collections.singletonList(123));
        response.setDatasets(Collections.singletonList(dataset));

        when(chartDataService.getChartData()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/chart-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels[0]").value("Test Label"))
                .andExpect(jsonPath("$.datasets[0].label").value("Test Dataset"))
                .andExpect(jsonPath("$.datasets[0].data[0]").value(123));
    }
}
