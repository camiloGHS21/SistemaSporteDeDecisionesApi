package com.example.demo.infrastructure.indicador;

import com.example.demo.application.Auth.JwtTokenProvider;
import com.example.demo.domain.core.DatoIndicador;
import com.example.demo.domain.core.DatoIndicadorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(IndicadorController.class)
class IndicadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatoIndicadorService datoIndicadorService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = {"USER"})
    void getDistinctIndicadores_shouldReturnOk() throws Exception {
        // Given
        when(datoIndicadorService.findDistinctIndicadores()).thenReturn(Collections.singletonList("IndicadorA"));

        // When & Then
        mockMvc.perform(get("/api/indicadores/nombres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("IndicadorA"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getIndicadoresPorPais_shouldReturnOk() throws Exception {
        // Given
        DatoIndicador indicador = new DatoIndicador();
        indicador.setTipoIndicador("IndicadorA");
        when(datoIndicadorService.findByPaisNombre("Mexico")).thenReturn(Collections.singletonList(indicador));

        // When & Then
        mockMvc.perform(get("/api/indicadores/pais/Mexico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoIndicador").value("IndicadorA"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getDistinctIndicadores_whenNoIndicadores_shouldReturnNoContent() throws Exception {
        // Given
        when(datoIndicadorService.findDistinctIndicadores()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/indicadores/nombres"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getIndicadoresPorPais_whenNoIndicadores_shouldReturnNoContent() throws Exception {
        // Given
        when(datoIndicadorService.findByPaisNombre("Mexico")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/indicadores/pais/Mexico"))
                .andExpect(status().isNoContent());
    }
}
