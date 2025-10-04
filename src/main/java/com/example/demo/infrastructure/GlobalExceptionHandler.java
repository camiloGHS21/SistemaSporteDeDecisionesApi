package com.example.demo.infrastructure;

import com.example.demo.domain.file.DuplicateFileContentException;
import com.example.demo.domain.file.FileAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<Object> handleFileAlreadyExistsException(FileAlreadyExistsException ex, WebRequest request) {
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateFileContentException.class)
    public ResponseEntity<Object> handleDuplicateFileContentException(DuplicateFileContentException ex, WebRequest request) {
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.CONFLICT);
    }
}
