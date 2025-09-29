package com.example.demo.domain.file;

import com.example.demo.domain.file.FileData;
import java.util.List;


public interface FileDataService {

    FileData save(FileData fileData);

    List<FileData> findAll();
}