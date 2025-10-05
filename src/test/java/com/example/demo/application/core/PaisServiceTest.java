package com.example.demo.application.core;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisRepository;

@ExtendWith(MockitoExtension.class)
class PaisServiceTest {

    @Mock
    private PaisRepository paisRepository;

    @InjectMocks
    private PaisServiceImpl paisService;

    @Test
    void testFindAll() {
        // Given
        Pais pais = new Pais();
        pais.setNombre_pais("Mexico");
        List<Pais> paises = Collections.singletonList(pais);
        when(paisRepository.findAll()).thenReturn(paises);

        // When
        List<Pais> result = paisService.findAll();

        // Then
        assertEquals(1, result.size());
        assertEquals("Mexico", result.get(0).getNombre_pais());
    }
}
