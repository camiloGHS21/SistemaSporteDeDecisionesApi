package com.example.demo.application.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;



public interface FileProcessingService {

    List<String> processFile(MultipartFile file);
}
