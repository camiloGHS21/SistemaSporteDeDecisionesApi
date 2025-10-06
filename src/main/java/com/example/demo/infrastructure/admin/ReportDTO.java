package com.example.demo.infrastructure.admin;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReportDTO {

    private Long id;
    private String title;
    private String user;
    private LocalDateTime date;
}
