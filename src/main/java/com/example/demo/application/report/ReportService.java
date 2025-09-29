package com.example.demo.application.report;

import com.example.demo.infrastructure.report.ReportRequest;

public interface ReportService {

    byte[] generateReport(ReportRequest request);
}
