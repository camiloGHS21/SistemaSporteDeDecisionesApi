package com.example.demo.application.core;

import com.example.demo.domain.core.DatoIndicador;
import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisRepository;
import com.example.demo.domain.file.ValidatedDataRow;
import com.example.demo.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatoIndicadorServiceTest {

    @Mock
    private DatoIndicadorRepository datoIndicadorRepository;

    @Mock
    private PaisRepository paisRepository;

    @InjectMocks
    private DatoIndicadorServiceImpl datoIndicadorService;

    @Test
    void testSaveDatosIndicador() {
        // Given
        ValidatedDataRow row = new ValidatedDataRow();
        row.setPaisNombre("Mexico");
        row.setName("IndicadorA");
        row.setAnio(2023);
        row.setValue(123.45f);
        row.setFuente("FuenteA");

        Pais pais = new Pais();
        pais.setNombre_pais("Mexico");

        User user = new User();

        when(paisRepository.findByNombrePais("Mexico")).thenReturn(Optional.of(pais));
        when(datoIndicadorRepository.existsByPaisAndTipoIndicadorAndAnioAndUser(pais, "IndicadorA", 2023, user)).thenReturn(false);

        // When
        List<String> errors = datoIndicadorService.saveDatosIndicador(Collections.singletonList(row), 1L, user);

        // Then
        assertEquals(0, errors.size());
    }

    @Test
    void testFindByUserAndPais_Nombre_paisIgnoreCase() {
        // Given
        User user = new User();
        DatoIndicador indicador = new DatoIndicador();
        indicador.setTipoIndicador("IndicadorA");
        List<DatoIndicador> indicadores = Collections.singletonList(indicador);
        when(datoIndicadorRepository.findByUserAndPais_Nombre_paisIgnoreCase(user, "mexico")).thenReturn(indicadores);

        // When
        List<DatoIndicador> result = datoIndicadorService.findByUserAndPais_Nombre_paisIgnoreCase(user, "mexico");

        // Then
        assertEquals(1, result.size());
        assertEquals("IndicadorA", result.get(0).getTipoIndicador());
    }

    @Test
    void testFindDistinctIndicadoresByUser() {
        // Given
        User user = new User();
        List<String> distinctIndicadores = Collections.singletonList("IndicadorA");
        when(datoIndicadorRepository.findDistinctTipoIndicadorByUser(user)).thenReturn(distinctIndicadores);

        // When
        List<String> result = datoIndicadorService.findDistinctIndicadoresByUser(user);

        // Then
        assertEquals(1, result.size());
        assertEquals("IndicadorA", result.get(0));
    }
}
