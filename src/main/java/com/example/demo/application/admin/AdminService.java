package com.example.demo.application.admin;

import java.util.List;

import com.example.demo.infrastructure.admin.DashboardStats;
import com.example.demo.infrastructure.admin.ReportDTO;
import com.example.demo.infrastructure.admin.UserDTO;

public interface AdminService {

    DashboardStats getDashboardStats();

    List<UserDTO> getUsers();

    UserDTO getUserById(Integer id);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Integer id, UserDTO userDTO);

    void deleteUser(Integer id);

    List<ReportDTO> getReports();

    ReportDTO getReportById(Long id);

    void deleteReport(Long id);
}
