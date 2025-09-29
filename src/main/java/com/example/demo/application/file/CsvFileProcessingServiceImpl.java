package com.example.demo.application.file;

import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.domain.file.ValidatedDataRow;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.file.AbstractFileProcessingService;



@Service("csvFileProcessingService")
public class CsvFileProcessingServiceImpl extends AbstractFileProcessingService {

    @Autowired
    public CsvFileProcessingServiceImpl(FileDataRepository fileDataRepository, Validator validator, ValidationService validationService) {
        super(fileDataRepository, validator, validationService);
    }

   
    @Override
    protected List<ValidatedDataRow> parseFile(MultipartFile file) {
        System.out.println("Processing CSV file: " + file.getOriginalFilename());
        List<ValidatedDataRow> dataRows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            // Omitir la fila de la cabecera
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length >= 4) {
                    ValidatedDataRow row = new ValidatedDataRow();
                    row.setName(nextLine[0]); // tipo_indicador
                    try {
                        row.setValue(Float.parseFloat(nextLine[1])); // valor
                    } catch (NumberFormatException e) {
                        // En una aplicación real, esto debería ser manejado por un sistema de logging.
                        continue; // Omitir fila si el valor no es un número válido
                    }
                    // Por simplicidad, no se parsean 'anio' y 'fuente' por ahora.
                    dataRows.add(row);
                }
            }
        } catch (IOException | CsvValidationException e) {
            // En una aplicación real, esto debería ser manejado por un sistema de logging.
            e.printStackTrace();
        }
        return dataRows;
    }

    @Override
    protected String getFileType() {
        return "CSV";
    }
}
