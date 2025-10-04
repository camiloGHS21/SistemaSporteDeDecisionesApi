package com.example.demo.domain.file;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "file_data", indexes = {
        @Index(name = "idx_filename", columnList = "fileName", unique = true),
        @Index(name = "idx_filehash", columnList = "fileHash")
})
public class FileData {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    private String fileType;

    private LocalDateTime processedDate;

    @Column(nullable = false)
    private String fileHash;

    private String data; // To store processed data, e.g., as JSON
}