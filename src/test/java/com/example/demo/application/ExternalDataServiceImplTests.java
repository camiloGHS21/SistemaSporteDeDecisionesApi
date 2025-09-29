package com.example.demo.application;

import com.example.demo.domain.external.ExternalData;
import com.example.demo.domain.external.ExternalDataRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.demo.application.external.ExternalDataServiceImpl;

@ExtendWith(MockitoExtension.class)
class ExternalDataServiceImplTests {

    @Mock
    private ExternalDataRepository externalDataRepository;

    @InjectMocks
    private ExternalDataServiceImpl externalDataService;

    @Test
    void findAll_shouldReturnAllData() {
        ExternalData externalData = new ExternalData();
        externalData.setCountry("Test Country");
        List<ExternalData> allData = Collections.singletonList(externalData);

        when(externalDataRepository.findAll()).thenReturn(allData);

        List<ExternalData> result = externalDataService.findAll();

        assertEquals(1, result.size());
        assertEquals("Test Country", result.get(0).getCountry());
    }
}
