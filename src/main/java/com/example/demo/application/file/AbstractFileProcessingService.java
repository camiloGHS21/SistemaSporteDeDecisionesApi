package com.example.demo.application.file;

import com.example.demo.domain.file.DuplicateFileContentException;
import com.example.demo.domain.file.FileAlreadyExistsException;
import com.example.demo.domain.file.FileData;
import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.domain.file.ValidatedDataRow;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.example.demo.application.validation.ValidationService;
import com.example.demo.domain.core.DatoIndicadorService;

public abstract class AbstractFileProcessingService implements FileProcessingService {

    private final FileDataRepository fileDataRepository;
    private final Validator validator;
    private final ValidationService validationService;
    private final DatoIndicadorService datoIndicadorService;

    public AbstractFileProcessingService(FileDataRepository fileDataRepository, Validator validator, ValidationService validationService, DatoIndicadorService datoIndicadorService) {
        this.fileDataRepository = fileDataRepository;
        this.validator = validator;
        this.validationService = validationService;
        this.datoIndicadorService = datoIndicadorService;
    }

    @Override
    public List<String> processFile(MultipartFile file) {
        try {
            // Step 1: Check for duplicates by filename
            if (fileDataRepository.existsByFileName(file.getOriginalFilename())) {
                throw new FileAlreadyExistsException("File with name '" + file.getOriginalFilename() + "' already exists.");
            }

            // Step 2: Check for duplicates by content hash
            byte[] fileBytes = file.getBytes();
            String fileHash = calculateSha256(fileBytes);
            if (fileDataRepository.existsByFileHash(fileHash)) {
                throw new DuplicateFileContentException("File with identical content already exists.");
            }

            // Proceed with parsing and validation if no duplicates are found
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
                FileData savedFileData = saveFileData(file, dataRows.size(), fileHash);
                List<String> saveErrors = datoIndicadorService.saveDatosIndicador(dataRows, savedFileData.getId());
                errors.addAll(saveErrors);
            }
            return errors;
        } catch (IOException | NoSuchAlgorithmException e) {
            // In a real application, this should be handled by a logging system.
            e.printStackTrace();
            // Return an error message to the user
            return List.of("Error processing file: " + e.getMessage());
        }
    }

    private String calculateSha256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    protected abstract List<ValidatedDataRow> parseFile(MultipartFile file);

    private FileData saveFileData(MultipartFile file, int rowCount, String fileHash) {
        FileData fileData = new FileData();
        fileData.setFileName(file.getOriginalFilename());
        fileData.setFileType(getFileType());
        fileData.setProcessedDate(LocalDateTime.now());
        fileData.setFileHash(fileHash);
        fileData.setData("Number of rows: " + rowCount);
        return fileDataRepository.save(fileData);
    }

    protected abstract String getFileType();
}