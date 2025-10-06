package com.example.demo.application.admin;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileDataRepository fileDataRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private InformeRepository informeRepository;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User user;
    private Role role;
    private Informe informe;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setNombre_rol("USER");

        user = new User();
        user.setUsuario_id(1);
        user.setNombre_usuario("testuser");
        user.setEmail("test@example.com");
        user.setRol(role);

        informe = new Informe();
        informe.setId(1L);
        informe.setNombre_informe("Test Report");
        informe.setUsuario(user);
        informe.setFecha_generacion(LocalDateTime.now());
    }

    @Test
    void getDashboardStats() {
        when(userRepository.count()).thenReturn(10L);
        when(fileDataRepository.count()).thenReturn(20L);

        DashboardStats stats = adminService.getDashboardStats();

        assertEquals(10L, stats.getTotalUsers());
        assertEquals(20L, stats.getReportsGenerated());
        assertEquals(1000L, stats.getSiteVisits());
        assertEquals(10L, stats.getOpenIssues());
    }

    @Test
    void getUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDTO> users = adminService.getUsers();

        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getNombre_usuario());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO userDTO = adminService.getUserById(1L);

        assertNotNull(userDTO);
        assertEquals("testuser", userDTO.getNombre_usuario());
    }

    @Test
    void createUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setNombre_usuario("newuser");
        userDTO.setEmail("new@example.com");
        userDTO.setPassword("password");
        userDTO.setRole("USER");

        when(roleRepository.findByNombreRol("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setUsuario_id(2);
            return savedUser;
        });

        UserDTO createdUser = adminService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals("newuser", createdUser.getNombre_usuario());
    }

    @Test
    void updateUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setNombre_usuario("updateduser");
        userDTO.setEmail("updated@example.com");
        userDTO.setPassword("newpassword");
        userDTO.setRole("USER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByNombreRol("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO updatedUser = adminService.updateUser(1L, userDTO);

        assertNotNull(updatedUser);
        assertEquals("updateduser", updatedUser.getNombre_usuario());
    }

    @Test
    void deleteUser() {
        adminService.deleteUser(1L);
    }

    @Test
    void getReports() {
        when(informeRepository.findAll()).thenReturn(Collections.singletonList(informe));

        List<ReportDTO> reports = adminService.getReports();

        assertEquals(1, reports.size());
        assertEquals("Test Report", reports.get(0).getTitle());
    }

    @Test
    void getReportById() {
        when(informeRepository.findById(1L)).thenReturn(Optional.of(informe));

        ReportDTO reportDTO = adminService.getReportById(1L);

        assertNotNull(reportDTO);
        assertEquals("Test Report", reportDTO.getTitle());
    }

    @Test
    void deleteReport() {
        adminService.deleteReport(1L);
    }

    @Test
    void getReportPdf() throws Exception {
        byte[] pdfContent = "PDF Content".getBytes();
        when(reportService.generateReportPdf(1L)).thenReturn(pdfContent);

        byte[] result = adminService.getReportPdf(1L);

        assertNotNull(result);
        assertEquals("PDF Content", new String(result));
    }
}
