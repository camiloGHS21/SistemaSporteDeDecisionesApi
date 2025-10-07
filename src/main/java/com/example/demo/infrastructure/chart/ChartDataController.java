package com.example.demo.infrastructure.chart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.chart.ChartDataService;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api")
public class ChartDataController {

    private final ChartDataService chartDataService;
    private final UserRepository userRepository;

    @Autowired
    public ChartDataController(ChartDataService chartDataService, UserRepository userRepository) {
        this.chartDataService = chartDataService;
        this.userRepository = userRepository;
    }
  
    @Operation(
        summary = "Obtener datos para gráficos",
        description = "Devuelve datos agregados y formateados para ser consumidos por gráficos en el frontend, basados en la información del usuario autenticado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Datos para gráficos obtenidos con éxito"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        }
    )
    @GetMapping("/chart-data")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ChartDataResponse> getChartData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        ChartDataResponse chartData = chartDataService.getChartData(user);
        return ResponseEntity.ok(chartData);
    }
}