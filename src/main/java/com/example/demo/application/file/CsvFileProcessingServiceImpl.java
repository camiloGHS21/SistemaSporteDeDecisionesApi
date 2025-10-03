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



import com.example.demo.domain.core.DatoIndicadorService;

@Service("csvFileProcessingService")
public class CsvFileProcessingServiceImpl extends AbstractFileProcessingService {

    @Autowired
    public CsvFileProcessingServiceImpl(FileDataRepository fileDataRepository, Validator validator, ValidationService validationService, DatoIndicadorService datoIndicadorService) {
        super(fileDataRepository, validator, validationService, datoIndicadorService);
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
                if (nextLine.length >= 5) { // Espera 5 columnas: pais, tipo_indicador, valor, anio, fuente
                    ValidatedDataRow row = new ValidatedDataRow();
                    row.setPaisNombre(nextLine[0]);
                    row.setName(nextLine[1]);
                    try {
                        row.setValue(Float.parseFloat(nextLine[2]));
                        row.setAnio(Integer.parseInt(nextLine[3]));
                    } catch (NumberFormatException e) {
                        // Loggear el error y omitir la fila si los valores numéricos no son válidos
                        System.err.println("Error parsing number in row: " + String.join(",", nextLine));
                        continue;
                    }
                    row.setFuente(nextLine[4]);
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
