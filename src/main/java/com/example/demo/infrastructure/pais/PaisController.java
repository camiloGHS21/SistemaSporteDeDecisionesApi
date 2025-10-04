package com.example.demo.infrastructure.pais;

import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/paises")
public class PaisController {

    private final PaisService paisService;

    public PaisController(PaisService paisService) {
        this.paisService = paisService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<Pais>> getAllPaises() {
        List<Pais> paises = paisService.findAll();
        if (paises.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(paises);
    }
}
