package com.example.demo.application;

import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisRepository;
import com.example.demo.domain.file.ValidatedDataRow;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.application.validation.ValidationService;
import com.example.demo.application.validation.ValidationServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ValidationServiceTest {

    private ValidationService validationService;

    @Mock
    private DatoIndicadorRepository datoIndicadorRepository;

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        validationService = new ValidationServiceImpl(datoIndicadorRepository, paisRepository, userRepository);
        when(paisRepository.findByNombrePais(anyString())).thenReturn(Optional.of(new Pais()));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByEmail("testuser")).thenReturn(Optional.of(new User()));
    }

    @Test
    void whenNamesAreUnique_thenNoErrors() {
        // Given
        List<ValidatedDataRow> rows = new ArrayList<>();
        rows.add(createRow("pais1", "name1", 2023, 10));
        rows.add(createRow("pais2", "name2", 2023, 20));

        // When
        List<String> errors = validationService.validateUniqueNames(rows);

        // Then
        assertTrue(errors.isEmpty());
    }

    @Test
    void whenNamesAreDuplicated_thenErrorIsReturned() {
        // Given
        List<ValidatedDataRow> rows = new ArrayList<>();
        rows.add(createRow("pais1", "name1", 2023, 10));
        rows.add(createRow("pais1", "name1", 2023, 20));

        // When
        List<String> errors = validationService.validateUniqueNames(rows);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("La combinación de país, indicador y año 'pais1-name1-2023' está duplicada en el archivo."));
    }

    @Test
    void whenListIsEmpty_thenNoErrors() {
        // Given
        List<ValidatedDataRow> rows = new ArrayList<>();

        // When
        List<String> errors = validationService.validateUniqueNames(rows);

        // Then
        assertTrue(errors.isEmpty());
    }

    @Test
    void whenNameIsNull_thenDuplicatesAreFound() {
        // Given
        List<ValidatedDataRow> rows = new ArrayList<>();
        rows.add(createRow("pais1", null, 2023, 10));
        rows.add(createRow("pais1", null, 2023, 20));

        // When
        List<String> errors = validationService.validateUniqueNames(rows);

        // Then
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("La combinación de país, indicador y año 'pais1-null-2023' está duplicada en el archivo."));
    }

    private ValidatedDataRow createRow(String paisNombre, String name, int anio, int value) {
        ValidatedDataRow row = new ValidatedDataRow();
        row.setPaisNombre(paisNombre);
        row.setName(name);
        row.setAnio(anio);
        row.setValue(value);
        return row;
    }
}
