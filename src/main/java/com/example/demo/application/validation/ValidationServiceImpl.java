package com.example.demo.application.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.demo.domain.file.ValidatedDataRow;


@Service
public class ValidationServiceImpl implements ValidationService {

   
    @Override
    public List<String> validateUniqueNames(List<ValidatedDataRow> rows) {
        List<String> errors = new ArrayList<>();
        if (rows == null) {
            return errors;
        }
        Set<String> names = new HashSet<>();
        for (ValidatedDataRow row : rows) {
            if (row.getName() != null && !names.add(row.getName())) {
                errors.add("El nombre '" + row.getName() + "' está duplicado en el archivo.");
            }
        }
        return errors;
    }

  
    @Override
    public List<String> validateValueSum(List<ValidatedDataRow> rows) {
        List<String> errors = new ArrayList<>();
        if (rows == null) {
            return errors;
        }
        long sum = 0;
        for (ValidatedDataRow row : rows) {
            sum += row.getValue();
        }
        if (sum > 1000000) {
            errors.add("La suma de todos los valores ( " + sum + ") no puede ser mayor que 1,000,000.");
        }
        return errors;
    }
}
