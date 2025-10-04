package com.example.demo.infrastructure.indicador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.core.DatoIndicador;
import com.example.demo.domain.core.DatoIndicadorService;

@RestController
@RequestMapping("/api/indicadores")
public class IndicadorController {

    private final DatoIndicadorService datoIndicadorService;

    @Autowired
    public IndicadorController(DatoIndicadorService datoIndicadorService) {
        this.datoIndicadorService = datoIndicadorService;
    }

    @GetMapping("/nombres")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<String>> getDistinctIndicadores() {
        List<String> indicadores = datoIndicadorService.findDistinctIndicadores();
        if (indicadores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(indicadores);
    }

    @GetMapping("/pais/{nombrePais}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<DatoIndicador>> getIndicadoresPorPais(@PathVariable String nombrePais) {
        List<DatoIndicador> indicadores = datoIndicadorService.findByPaisNombre(nombrePais);
        if (indicadores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(indicadores);
    }
}
