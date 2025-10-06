package com.example.demo.application.admin;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.example.demo.infrastructure.admin.DashboardStats;
import com.example.demo.infrastructure.admin.ReportDTO;
import com.example.demo.infrastructure.admin.UserDTO;

public interface AdminService {

    DashboardStats getDashboardStats();

    List<UserDTO> getUsers();

    UserDTO getUserById(Long id);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);

    List<ReportDTO> getReports();

    ReportDTO getReportById(Long id);

    void deleteReport(Long id);

    byte[] getReportPdf(Long id);
}
