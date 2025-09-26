package com.example.demo.application.external;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.domain.external.ExternalData;
import com.example.demo.domain.external.ExternalDataRepository;
import com.example.demo.domain.external.ExternalDataService;



@Service
public class ExternalDataServiceImpl implements ExternalDataService {

    private final ExternalDataRepository externalDataRepository;

 
    public ExternalDataServiceImpl(ExternalDataRepository externalDataRepository) {
        this.externalDataRepository = externalDataRepository;
    }


    @Override
    public ExternalData save(ExternalData externalData) {
        return externalDataRepository.save(externalData);
    }

    @Override
    public List<ExternalData> findAll() {
        return externalDataRepository.findAll();
    }
}