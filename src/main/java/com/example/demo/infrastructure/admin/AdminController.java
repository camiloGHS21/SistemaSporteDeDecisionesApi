package com.example.demo.infrastructure.admin;

import java.util.List;

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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard-stats")
    public DashboardStats getDashboardStats() {
        return adminService.getDashboardStats();
    }

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return adminService.getUsers();
    }

    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        return adminService.getUserById(id);
    }

    @PostMapping("/users")
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return adminService.createUser(userDTO);
    }

    @PutMapping("/users/{id}")
    public UserDTO updateUser(@PathVariable Integer id, @RequestBody UserDTO userDTO) {
        return adminService.updateUser(id, userDTO);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Integer id) {
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
}
