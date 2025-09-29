package com.example.demo.application;

import com.example.demo.domain.file.FileData;
import com.example.demo.domain.file.FileDataRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.demo.application.file.FileDataServiceImpl;

@ExtendWith(MockitoExtension.class)
class FileDataServiceImplTests {

    @Mock
    private FileDataRepository fileDataRepository;

    @InjectMocks
    private FileDataServiceImpl fileDataService;

    @Test
    void findAll_shouldReturnAllFiles() {
        FileData fileData = new FileData();
        fileData.setFileName("test.csv");
        List<FileData> allFiles = Collections.singletonList(fileData);

        when(fileDataRepository.findAll()).thenReturn(allFiles);

        List<FileData> result = fileDataService.findAll();

        assertEquals(1, result.size());
        assertEquals("test.csv", result.get(0).getFileName());
    }
}
