package com.example.demo.infrastructure.file;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.application.file.FileProcessingService;
import com.example.demo.infrastructure.ValidationErrorResponse;


@RestController
@RequestMapping("/api")
public class FileUploadController {
  
    private final FileProcessingService csvFileProcessingService;
    
    private final FileProcessingService excelFileProcessingService;
  
    @Autowired
    public FileUploadController(
            @Qualifier("csvFileProcessingService") FileProcessingService csvFileProcessingService,
            @Qualifier("excelFileProcessingService") FileProcessingService excelFileProcessingService) {
        this.csvFileProcessingService = csvFileProcessingService;
        this.excelFileProcessingService = excelFileProcessingService;
    }

   

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file.");
        }

        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            List<String> errors;
            if (fileName.endsWith(".csv")) {
                errors = csvFileProcessingService.processFile(file);
                if (errors.isEmpty()) {
                    return ResponseEntity.ok().body("CSV file uploaded and processing started.");
                } else {
                    return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
                }
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                errors = excelFileProcessingService.processFile(file);
                if (errors.isEmpty()) {
                    return ResponseEntity.ok().body("Excel file uploaded and processing started.");
                } else {
                    return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
                }
            }
        }

        return ResponseEntity.badRequest().body("Unsupported file type.");
    }
}