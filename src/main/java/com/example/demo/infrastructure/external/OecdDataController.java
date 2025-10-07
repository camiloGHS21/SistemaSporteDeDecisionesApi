package com.example.demo.infrastructure.external;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.external.OecdApiService;
import com.example.demo.domain.external.OecdData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;




@RestController
@RequestMapping("/api/oecd-data")
public class OecdDataController {


    private final OecdApiService oecdApiService;


    public OecdDataController(OecdApiService oecdApiService) {
        this.oecdApiService = oecdApiService;
    }



    @Operation(
        summary = "Obtener datos de la OECD",
        description = "Consulta la API externa de la OECD para obtener datos económicos de un país y año específicos.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Datos obtenidos con éxito"),
            @ApiResponse(responseCode = "404", description = "Datos no encontrados para los parámetros especificados")
        }
    )
    @GetMapping("/{countryCode}/{year}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public Mono<List<OecdData>> getOecdData(@PathVariable String countryCode, @PathVariable String year) {
        return oecdApiService.getOecdData(countryCode, year);
    }
}