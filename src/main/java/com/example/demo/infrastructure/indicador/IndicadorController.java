package com.example.demo.infrastructure.indicador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.core.DatoIndicador;
import com.example.demo.domain.core.DatoIndicadorService;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;

@RestController
@RequestMapping("/api/indicadores")
public class IndicadorController {

    private final DatoIndicadorService datoIndicadorService;
    private final UserRepository userRepository;

    @Autowired
    public IndicadorController(DatoIndicadorService datoIndicadorService, UserRepository userRepository) {
        this.datoIndicadorService = datoIndicadorService;
        this.userRepository = userRepository;
    }

    @GetMapping("/nombres")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<String>> getDistinctIndicadores() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        List<String> indicadores = datoIndicadorService.findDistinctIndicadoresByUser(user);
        if (indicadores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(indicadores);
    }

    @GetMapping("/pais/{nombrePais}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<DatoIndicador>> getIndicadoresPorPais(@PathVariable String nombrePais) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        List<DatoIndicador> indicadores = datoIndicadorService.findByUserAndPais_Nombre_paisIgnoreCase(user, nombrePais);
        if (indicadores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(indicadores);
    }
}
