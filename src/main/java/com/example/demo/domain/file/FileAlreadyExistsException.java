package com.example.demo.domain.file;

public class FileAlreadyExistsException extends RuntimeException {
    public FileAlreadyExistsException(String message) {
        super(message);
    }
}
