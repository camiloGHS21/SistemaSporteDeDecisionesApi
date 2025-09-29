package com.example.demo.application.chart;

import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.infrastructure.chart.ChartDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ChartDataServiceImpl implements ChartDataService {


    private final FileDataRepository fileDataRepository;

    @Autowired
    public ChartDataServiceImpl(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

    @Override
    public ChartDataResponse getChartData() {

        long fileCount = fileDataRepository.count();

        ChartDataResponse response = new ChartDataResponse();
        response.setLabels(Collections.singletonList("Processed Files"));

        ChartDataResponse.Dataset dataset = new ChartDataResponse.Dataset();
        dataset.setLabel("Number of Files");
        dataset.setData(Collections.singletonList(fileCount));

        response.setDatasets(Collections.singletonList(dataset));

        return response;
    }
}
