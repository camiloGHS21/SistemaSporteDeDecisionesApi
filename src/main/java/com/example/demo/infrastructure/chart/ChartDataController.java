package com.example.demo.infrastructure.chart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.chart.ChartDataService;


@RestController
@RequestMapping("/api")
public class ChartDataController {


    private final ChartDataService chartDataService;


    @Autowired
    public ChartDataController(ChartDataService chartDataService) {
        this.chartDataService = chartDataService;
    }
  
    @GetMapping("/chart-data")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ChartDataResponse> getChartData() {
        ChartDataResponse chartData = chartDataService.getChartData();
        return ResponseEntity.ok(chartData);
    }
}