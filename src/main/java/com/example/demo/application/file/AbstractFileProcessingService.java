package com.example.demo.application.file;

import com.example.demo.domain.file.DuplicateFileContentException;
import com.example.demo.domain.file.FileAlreadyExistsException;
import com.example.demo.domain.file.FileData;
import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.domain.file.ValidatedDataRow;
import com.example.demo.domain.user.UserRepository;
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
import com.example.demo.domain.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

public abstract class AbstractFileProcessingService implements FileProcessingService {

    private final FileDataRepository fileDataRepository;
    private final Validator validator;
    private final ValidationService validationService;
    private final DatoIndicadorService datoIndicadorService;
    private final UserRepository userRepository;

    public AbstractFileProcessingService(FileDataRepository fileDataRepository, Validator validator, ValidationService validationService, DatoIndicadorService datoIndicadorService, UserRepository userRepository) {
        this.fileDataRepository = fileDataRepository;
        this.validator = validator;
        this.validationService = validationService;
        this.datoIndicadorService = datoIndicadorService;
        this.userRepository = userRepository;
    }

    @Override
    public List<String> processFile(MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

            // Step 1: Check for duplicates by filename for the same user
            if (fileDataRepository.existsByFileNameAndUser(file.getOriginalFilename(), user)) {
                throw new FileAlreadyExistsException("File with name '" + file.getOriginalFilename() + "' already exists for this user.");
            }

            // Step 2: Check for duplicates by content hash for the same user
            byte[] fileBytes = file.getBytes();
            String fileHash = calculateSha256(fileBytes);
            if (fileDataRepository.existsByFileHashAndUser(fileHash, user)) {
                throw new DuplicateFileContentException("File with identical content already exists for this user.");
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
                FileData savedFileData = saveFileData(file, dataRows.size(), fileHash, user);
                List<String> saveErrors = datoIndicadorService.saveDatosIndicador(dataRows, savedFileData.getId(), user);
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

    private FileData saveFileData(MultipartFile file, int rowCount, String fileHash, User user) {
        FileData fileData = new FileData();
        fileData.setFileName(file.getOriginalFilename());
        fileData.setFileType(getFileType());
        fileData.setProcessedDate(LocalDateTime.now());
        fileData.setFileHash(fileHash);
        fileData.setData("Number of rows: " + rowCount);
        fileData.setUser(user);
        return fileDataRepository.save(fileData);
    }

    protected abstract String getFileType();
}