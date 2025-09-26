package com.example.demo.domain.external;

import java.util.List;


public interface ExternalDataService {

    ExternalData save(ExternalData externalData);

    List<ExternalData> findAll();
}