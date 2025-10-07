package com.example.demo.infrastructure.admin;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.admin.AdminService;
import com.example.demo.application.report.ReportService;
import com.example.demo.domain.report.Informe;
import com.example.demo.domain.report.InformeRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;
    private final InformeRepository informeRepository;

    @Operation(summary = "Validar acceso de administrador", description = "Verifica si el usuario actual tiene rol de administrador.")
    @GetMapping("/auth/validate")
    public ResponseEntity<Boolean> validateAdmin() {
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "Obtener estadísticas del dashboard", description = "Devuelve estadísticas agregadas para el panel de administración.")
    @GetMapping("/dashboard-stats")
    public DashboardStats getDashboardStats() {
        return adminService.getDashboardStats();
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista de todos los usuarios del sistema.")
    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return adminService.getUsers();
    }

    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve los detalles de un usuario específico.")
    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return adminService.getUserById(id);
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario en el sistema.")
    @PostMapping("/users")
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return adminService.createUser(userDTO);
    }

    @Operation(summary = "Actualizar un usuario", description = "Actualiza los datos de un usuario existente.")
    @PutMapping("/users/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return adminService.updateUser(id, userDTO);
    }

    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario del sistema.")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
    }

    @Operation(summary = "Obtener todos los informes", description = "Devuelve una lista de todos los informes generados.")
    @GetMapping("/reports")
    public List<ReportDTO> getReports() {
        return adminService.getReports();
    }

    @Operation(summary = "Obtener un informe por ID", description = "Devuelve los detalles de un informe específico.")
    @GetMapping("/reports/{id}")
    public ReportDTO getReportById(@PathVariable Long id) {
        return adminService.getReportById(id);
    }

    @Operation(summary = "Eliminar un informe", description = "Elimina un informe del sistema.")
    @DeleteMapping("/reports/{id}")
    public void deleteReport(@PathVariable Long id) {
        adminService.deleteReport(id);
    }

    @Operation(summary = "Descargar un informe en PDF", description = "Genera y descarga un informe específico en formato PDF.")
    @GetMapping("/reports/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {
        try {
            byte[] pdf = reportService.generateReportPdf(id);
            Informe informe = informeRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
            String filename = informe.getNombre_informe();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename + ".pdf");
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
