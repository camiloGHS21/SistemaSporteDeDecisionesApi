package com.example.demo.infrastructure.report;

import java.util.List;

import lombok.Data;


@Data
public class ReportRequest {

    private String reportName;

    private String paisPrincipal;

    private List<String> paises;

    private List<String> indicadores;
    
    private String reportType;
}