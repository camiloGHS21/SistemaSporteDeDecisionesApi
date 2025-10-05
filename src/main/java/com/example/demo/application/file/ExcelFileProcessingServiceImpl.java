package com.example.demo.application.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.application.validation.ValidationService;
import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.domain.file.ValidatedDataRow;
import com.example.demo.application.file.AbstractFileProcessingService;

import jakarta.validation.Validator;



import com.example.demo.domain.core.DatoIndicadorService;

@Service("excelFileProcessingService")
public class ExcelFileProcessingServiceImpl extends AbstractFileProcessingService {

    @Autowired
    public ExcelFileProcessingServiceImpl(FileDataRepository fileDataRepository, Validator validator, ValidationService validationService, DatoIndicadorService datoIndicadorService) {
        super(fileDataRepository, validator, validationService, datoIndicadorService);
    }


    @Override
    protected List<ValidatedDataRow> parseFile(MultipartFile file) {
        System.out.println("Processing Excel file: " + file.getOriginalFilename());
        List<ValidatedDataRow> dataRows = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                if (row.getLastCellNum() >= 5) {
                    ValidatedDataRow dataRow = new ValidatedDataRow();
                    dataRow.setPaisNombre(row.getCell(0).getStringCellValue());
                    dataRow.setName(row.getCell(1).getStringCellValue());
                    try {
                        dataRow.setValue((float) row.getCell(2).getNumericCellValue());
                        dataRow.setAnio((int) row.getCell(3).getNumericCellValue());
                    } catch (IllegalStateException e) {
                        System.err.println("Error parsing number in row: " + row.getRowNum());
                        continue;
                    }
                    Cell fuenteCell = row.getCell(4);
                    if (fuenteCell != null) {
                        dataRow.setFuente(fuenteCell.getStringCellValue());
                    }
                    dataRows.add(dataRow);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataRows;
    }

    @Override
    protected String getFileType() {
        return "EXCEL";
    }
}
