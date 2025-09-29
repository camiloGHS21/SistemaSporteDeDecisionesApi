package com.example.demo.application.file;

import com.example.demo.domain.file.FileData;
import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.domain.file.FileDataService;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class FileDataServiceImpl implements FileDataService {


    private final FileDataRepository fileDataRepository;

 
    public FileDataServiceImpl(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

 
    @Override
    public FileData save(FileData fileData) {
        return fileDataRepository.save(fileData);
    }

    @Override
    public List<FileData> findAll() {
        return fileDataRepository.findAll();
    }
}