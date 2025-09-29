package com.example.demo.application;

import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.infrastructure.chart.ChartDataResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.demo.application.chart.ChartDataServiceImpl;

@ExtendWith(MockitoExtension.class)
class ChartDataServiceTest {

    @Mock
    private FileDataRepository fileDataRepository;

    @InjectMocks
    private ChartDataServiceImpl chartDataService;

    @Test
    void testGetChartData() {
        // Given
        when(fileDataRepository.count()).thenReturn(5L);

        // When
        ChartDataResponse response = chartDataService.getChartData();

        // Then
        assertEquals(1, response.getLabels().size());
        assertEquals("Processed Files", response.getLabels().get(0));

        assertEquals(1, response.getDatasets().size());
        ChartDataResponse.Dataset dataset = response.getDatasets().get(0);
        assertEquals("Number of Files", dataset.getLabel());
        assertEquals(1, dataset.getData().size());
        assertEquals(5L, dataset.getData().get(0));
    }
}
