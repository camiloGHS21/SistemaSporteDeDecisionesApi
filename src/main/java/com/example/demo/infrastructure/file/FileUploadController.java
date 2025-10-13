package com.example.demo.infrastructure.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.application.file.FileProcessingService;
import com.example.demo.infrastructure.ValidationErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping("/api")
public class FileUploadController {
  
    private final FileProcessingService csvFileProcessingService;
    
    private final FileProcessingService excelFileProcessingService;
  
    @Autowired
    public FileUploadController(
            @Qualifier("csvFileProcessingService") FileProcessingService csvFileProcessingService,
            @Qualifier("excelFileProcessingService") FileProcessingService excelFileProcessingService) {
        this.csvFileProcessingService = csvFileProcessingService;
        this.excelFileProcessingService = excelFileProcessingService;
    }

   

    @Operation(
        summary = "Subir un archivo de datos (CSV o Excel)",
        description = "Procesa un archivo CSV o Excel para extraer y validar datos de indicadores. Devuelve errores de validación si los hay.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Archivo subido y procesándose"),
            @ApiResponse(responseCode = "400", description = "Archivo no soportado, vacío o con errores de validación")
        }
    )
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a file.");
        }

        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            List<String> errors;
            if (fileName.endsWith(".csv")) {
                errors = csvFileProcessingService.processFile(file);
                if (errors.isEmpty()) {
                    return ResponseEntity.ok().body("CSV file uploaded and processing started.");
                } else {
                    return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
                }
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                errors = excelFileProcessingService.processFile(file);
                if (errors.isEmpty()) {
                    return ResponseEntity.ok().body("Excel file uploaded and processing started.");
                } else {
                    return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
                }
            }
        }

        return ResponseEntity.badRequest().body("Unsupported file type.");
    }

    @Operation(
        summary = "Sube un fragmento de un archivo",
        description = "Sube un fragmento de un archivo grande. Cuando se recibe el último fragmento, el archivo se reensambla y se procesa.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Fragmento recibido o archivo procesándose"),
            @ApiResponse(responseCode = "400", description = "Tipo de archivo no soportado o error de validación"),
            @ApiResponse(responseCode = "500", description = "Error procesando el fragmento del archivo")
        }
    )
    @PostMapping("/upload-chunk")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> uploadChunk(@RequestParam("file") MultipartFile file,
                                         @RequestParam("chunkNumber") int chunkNumber,
                                         @RequestParam("totalChunks") int totalChunks,
                                         @RequestParam("uploadId") String uploadId,
                                         @RequestParam("fileName") String fileName) {
        try {
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "file-uploads", uploadId);
            Files.createDirectories(tempDir);
            Path chunkFile = tempDir.resolve(String.valueOf(chunkNumber));
            file.transferTo(chunkFile);

            if (chunkNumber == totalChunks - 1) { // Last chunk
                Path finalFile = tempDir.resolve(fileName);
                try (OutputStream fileOutputStream = Files.newOutputStream(finalFile)) {
                    for (int i = 0; i < totalChunks; i++) {
                        Path chunk = tempDir.resolve(String.valueOf(i));
                        if (Files.exists(chunk)) {
                            Files.copy(chunk, fileOutputStream);
                            Files.delete(chunk);
                        } else {
                            throw new IOException("Missing chunk: " + i + " for uploadId: " + uploadId);
                        }
                    }
                }

                String contentType = Files.probeContentType(finalFile);
                if (contentType == null) {
                    if (fileName.endsWith(".csv")) {
                        contentType = "text/csv";
                    } else if (fileName.endsWith(".xls")) {
                        contentType = "application/vnd.ms-excel";
                    } else if (fileName.endsWith(".xlsx")) {
                        contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    } else {
                        contentType = "application/octet-stream";
                    }
                }

                MultipartFile reassembledFile = new ReassembledMultipartFile(finalFile, fileName, contentType);

                List<String> errors;
                if (fileName.endsWith(".csv")) {
                    errors = csvFileProcessingService.processFile(reassembledFile);
                } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                    errors = excelFileProcessingService.processFile(reassembledFile);
                } else {
                    try {
                        Files.deleteIfExists(finalFile);
                        Files.deleteIfExists(tempDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return ResponseEntity.badRequest().body("Unsupported file type.");
                }

                try {
                    Files.deleteIfExists(finalFile);
                    Files.deleteIfExists(tempDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (errors.isEmpty()) {
                    return ResponseEntity.ok().body("File reassembled and processing started.");
                } else {
                    return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
                }

            } else {
                return ResponseEntity.ok().body("Chunk " + chunkNumber + " of " + totalChunks + " received.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing file chunk: " + e.getMessage());
        }
    }

    private static class ReassembledMultipartFile implements MultipartFile {
        private final Path file;
        private final String originalFilename;
        private final String contentType;

        public ReassembledMultipartFile(Path file, String originalFilename, String contentType) {
            this.file = file;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            try {
                return Files.size(file) == 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public long getSize() {
            try {
                return Files.size(file);
            } catch (IOException e) {
                return 0;
            }
        }

        @Override
        public byte[] getBytes() throws IOException {
            return Files.readAllBytes(file);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return Files.newInputStream(file);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            Files.copy(file, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}