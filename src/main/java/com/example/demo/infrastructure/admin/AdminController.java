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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;
    private final InformeRepository informeRepository;

    @GetMapping("/auth/validate")
    public ResponseEntity<Boolean> validateAdmin() {
        return ResponseEntity.ok(true);
    }

    @GetMapping("/dashboard-stats")
    public DashboardStats getDashboardStats() {
        return adminService.getDashboardStats();
    }

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return adminService.getUsers();
    }

    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return adminService.getUserById(id);
    }

    @PostMapping("/users")
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return adminService.createUser(userDTO);
    }

    @PutMapping("/users/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return adminService.updateUser(id, userDTO);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
    }

    @GetMapping("/reports")
    public List<ReportDTO> getReports() {
        return adminService.getReports();
    }

    @GetMapping("/reports/{id}")
    public ReportDTO getReportById(@PathVariable Long id) {
        return adminService.getReportById(id);
    }

    @DeleteMapping("/reports/{id}")
    public void deleteReport(@PathVariable Long id) {
        adminService.deleteReport(id);
    }

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
