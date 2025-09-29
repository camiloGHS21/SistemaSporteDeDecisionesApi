package com.example.demo.infrastructure.chart;

import lombok.Data;

import java.util.List;

@Data
public class ChartDataResponse {

    private List<String> labels;

    private List<Dataset> datasets;

    @Data
    public static class Dataset {

        private String label;

        private List<Number> data;
    }
}