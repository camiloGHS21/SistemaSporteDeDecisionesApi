package com.example.demo.infrastructure.admin;

import lombok.Data;

@Data
public class DashboardStats {

    private long totalUsers;
    private long reportsGenerated;
    private long siteVisits;
    private long openIssues;
}
