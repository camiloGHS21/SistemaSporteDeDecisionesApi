package com.example.demo.domain.file;

public class DuplicateFileContentException extends RuntimeException {
    public DuplicateFileContentException(String message) {
        super(message);
    }
}
