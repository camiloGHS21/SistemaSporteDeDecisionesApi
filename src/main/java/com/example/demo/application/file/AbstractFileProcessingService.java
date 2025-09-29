package com.example.demo.application.file;

import com.example.demo.domain.file.FileData;
import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.domain.file.ValidatedDataRow;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.file.FileProcessingService;



public abstract class AbstractFileProcessingService implements FileProcessingService {

  
    private final FileDataRepository fileDataRepository;
   
    private final Validator validator;
  
    private final ValidationService validationService;

 
    public AbstractFileProcessingService(FileDataRepository fileDataRepository, Validator validator, ValidationService validationService) {
        this.fileDataRepository = fileDataRepository;
        this.validator = validator;
        this.validationService = validationService;
    }

   
    @Override
    public List<String> processFile(MultipartFile file) {
        List<ValidatedDataRow> dataRows = parseFile(file);
        List<String> errors = new ArrayList<>();
        errors.addAll(validationService.validateUniqueNames(dataRows));
        errors.addAll(validationService.validateValueSum(dataRows));
        for (ValidatedDataRow row : dataRows) {
            Set<ConstraintViolation<ValidatedDataRow>> violations = validator.validate(row);
            if (!violations.isEmpty()) {
                for (ConstraintViolation<ValidatedDataRow> violation : violations) {
                    errors.add(violation.getMessage());
                }
            }
        }

        if (errors.isEmpty() && !dataRows.isEmpty()) {
            saveFileData(file, dataRows.size());
        }
        return errors;
    }

 
    protected abstract List<ValidatedDataRow> parseFile(MultipartFile file);

 
    private void saveFileData(MultipartFile file, int rowCount) {
        FileData fileData = new FileData();
        fileData.setFileName(file.getOriginalFilename());
        fileData.setFileType(getFileType());
        fileData.setProcessedDate(LocalDateTime.now());
        fileData.setData("Number of rows: " + rowCount);
        fileDataRepository.save(fileData);
    }

   
    protected abstract String getFileType();
}