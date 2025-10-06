package com.example.demo.application.chart;

import com.example.demo.domain.user.User;
import com.example.demo.infrastructure.chart.ChartDataResponse;

public interface ChartDataService {

    ChartDataResponse getChartData(User user);
}
