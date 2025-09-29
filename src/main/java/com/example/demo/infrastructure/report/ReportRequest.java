package com.example.demo.infrastructure.report;

import java.util.List;

import lombok.Data;


@Data
public class ReportRequest {

    private String reportName;

    private List<Long> fileIds;
    
    private String reportType;
}