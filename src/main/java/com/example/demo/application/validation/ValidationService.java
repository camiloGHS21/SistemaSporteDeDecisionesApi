package com.example.demo.application.validation;

import com.example.demo.domain.file.ValidatedDataRow;

import java.util.List;


public interface ValidationService {

 
    List<String> validateUniqueNames(List<ValidatedDataRow> rows);

  
    List<String> validateValueSum(List<ValidatedDataRow> rows);
}