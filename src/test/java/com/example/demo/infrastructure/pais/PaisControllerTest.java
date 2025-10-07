package com.example.demo.infrastructure.pais;

import com.example.demo.application.Auth.JwtTokenProvider;
import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisService;
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

@WebMvcTest(PaisController.class)
class PaisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaisService paisService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = {"USER"})
    void getAllPaises_shouldReturnOk() throws Exception {
        // Given
        Pais pais = new Pais();
        pais.setNombre_pais("Mexico");
        when(paisService.findAll()).thenReturn(Collections.singletonList(pais));

        // When & Then
        mockMvc.perform(get("/api/paises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre_pais").value("Mexico"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getAllPaises_whenNoPaises_shouldReturnNoContent() throws Exception {
        // Given
        when(paisService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/paises"))
                .andExpect(status().isNoContent());
    }
}
