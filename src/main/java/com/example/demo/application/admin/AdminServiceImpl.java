package com.example.demo.application.admin;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.application.report.ReportService;
import com.example.demo.domain.file.FileDataRepository;
import com.example.demo.domain.report.Informe;
import com.example.demo.domain.report.InformeRepository;
import com.example.demo.domain.user.Role;
import com.example.demo.domain.user.RoleRepository;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;
import com.example.demo.infrastructure.admin.DashboardStats;
import com.example.demo.infrastructure.admin.ReportDTO;
import com.example.demo.infrastructure.admin.UserDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final FileDataRepository fileDataRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final InformeRepository informeRepository;
    private final ReportService reportService;

    @Override
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalUsers(userRepository.count());
        stats.setReportsGenerated(fileDataRepository.count());
        stats.setSiteVisits(1000); // Mock data
        stats.setOpenIssues(10); // Mock data
        return stats;
    }

    @Override
    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toUserDTO)
                .orElse(null);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setNombre_usuario(userDTO.getNombre_usuario());
        user.setEmail(userDTO.getEmail());
        user.setContrasena_hash(passwordEncoder.encode(userDTO.getPassword()));
        Role userRole = roleRepository.findByNombreRol(userDTO.getRole()).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRol(userRole);
        return toUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setNombre_usuario(userDTO.getNombre_usuario());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setContrasena_hash(passwordEncoder.encode(userDTO.getPassword()));
        }
        Role userRole = roleRepository.findByNombreRol(userDTO.getRole()).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRol(userRole);
        return toUserDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<ReportDTO> getReports() {
        return informeRepository.findAll().stream()
                .map(this::toReportDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReportDTO getReportById(Long id) {
        return informeRepository.findById(id)
                .map(this::toReportDTO)
                .orElse(null);
    }

    @Override
    public void deleteReport(Long id) {
        informeRepository.deleteById(id);
    }

    @Override
    public byte[] getReportPdf(Long id) {
        try {
            return reportService.generateReportPdf(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private ReportDTO toReportDTO(Informe informe) {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(informe.getId());
        reportDTO.setTitle(informe.getNombre_informe());
        reportDTO.setUser(informe.getUsuario().getNombre_usuario());
        reportDTO.setDate(informe.getFecha_generacion());
        return reportDTO;
    }

    private UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getUsuario_id());
        userDTO.setNombre_usuario(user.getNombre_usuario());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRol().getNombre_rol());
        userDTO.setStatus("Activo"); // Mock data
        return userDTO;
    }
}
