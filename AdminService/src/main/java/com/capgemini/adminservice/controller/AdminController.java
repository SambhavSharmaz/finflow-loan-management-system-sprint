package com.capgemini.adminservice.controller;

import com.capgemini.adminservice.dto.ApiResponse;
import com.capgemini.adminservice.dto.DecisionRequest;
import com.capgemini.adminservice.dto.DecisionResponse;
import com.capgemini.adminservice.dto.ReportResponse;
import com.capgemini.adminservice.dto.UserDTO;
import com.capgemini.adminservice.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/applications/{id}/decision")
    public ApiResponse<DecisionResponse> decision(
            @PathVariable Long id,
            @RequestParam String status) {
        DecisionRequest req = new DecisionRequest();
        req.setStatus(status);
        DecisionResponse response = adminService.makeDecision(id, req);
        return new ApiResponse<>(true, "Decision recorded.", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/applications")
    public List<com.capgemini.adminservice.dto.ApplicationDTO> getAllApplications() {
        return adminService.getAllApplications();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ApiResponse<java.util.Map<String, Object>> getStats() {
        return new ApiResponse<>(true, "Stats fetched.", adminService.getStats());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reports/generate")
    public ApiResponse<ReportResponse> generateReport() {
        ReportResponse report = adminService.generateReport();
        return new ApiResponse<>(true, "Report generated.", report);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports")
    public ApiResponse<List<ReportResponse>> getReports() {
        List<ReportResponse> reports = adminService.getReportHistory();
        return new ApiResponse<>(true, "Reports fetched.", reports);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ApiResponse<List<UserDTO>> getAllUsers() {
        return new ApiResponse<>(true, "Users fetched.", adminService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}")
    public ApiResponse<UserDTO> updateUser(@PathVariable Long id, @RequestParam String role) {
        UserDTO updated = adminService.updateUserRole(id, role);
        return new ApiResponse<>(true, "User updated.", updated);
    }
}
