package com.example.demo.infrastructure.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.report.ReportService;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    @Autowired
    public ReportController(ReportService reportService, UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @PostMapping("/reports")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<byte[]> generateReport(@RequestBody ReportRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        byte[] contents = reportService.generateReport(request, user);

        HttpHeaders headers = new HttpHeaders();
        String filename = request.getReportName();

        // Gap analysis is always PDF
        if (request.getPaisPrincipal() != null && !request.getPaisPrincipal().isEmpty()) {
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename + ".pdf");
        } else if ("CSV".equalsIgnoreCase(request.getReportType())) {
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filename + ".csv");
        } else {
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename + ".pdf");
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(contents);
    }
}