package com.example.demo.infrastructure.indicador;

import com.example.demo.application.Auth.JwtTokenProvider;
import com.example.demo.domain.core.DatoIndicador;
import com.example.demo.domain.core.DatoIndicadorService;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private UserRepository userRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void getDistinctIndicadores_shouldReturnOk() throws Exception {
        // Given
        User mockUser = new User();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
        when(datoIndicadorService.findDistinctIndicadoresByUser(any(User.class))).thenReturn(Collections.singletonList("IndicadorA"));

        // When & Then
        mockMvc.perform(get("/api/indicadores/nombres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("IndicadorA"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void getIndicadoresPorPais_shouldReturnOk() throws Exception {
        // Given
        User mockUser = new User();
        DatoIndicador indicador = new DatoIndicador();
        indicador.setTipoIndicador("IndicadorA");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
        when(datoIndicadorService.findByUserAndPais_Nombre_paisIgnoreCase(any(User.class), eq("Mexico"))).thenReturn(Collections.singletonList(indicador));

        // When & Then
        mockMvc.perform(get("/api/indicadores/pais/Mexico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoIndicador").value("IndicadorA"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void getDistinctIndicadores_whenNoIndicadores_shouldReturnNoContent() throws Exception {
        // Given
        User mockUser = new User();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
        when(datoIndicadorService.findDistinctIndicadoresByUser(any(User.class))).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/indicadores/nombres"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = {"USER"})
    void getIndicadoresPorPais_whenNoIndicadores_shouldReturnNoContent() throws Exception {
        // Given
        User mockUser = new User();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
        when(datoIndicadorService.findByUserAndPais_Nombre_paisIgnoreCase(any(User.class), eq("Mexico"))).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/indicadores/pais/Mexico"))
                .andExpect(status().isNoContent());
    }
}
