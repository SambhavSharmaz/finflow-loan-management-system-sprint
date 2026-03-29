package com.capgemini.adminservice.controller;

import com.capgemini.adminservice.dto.ApiResponse;
import com.capgemini.adminservice.dto.DecisionRequest;
import com.capgemini.adminservice.dto.DecisionResponse;
import com.capgemini.adminservice.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/applications/{id}/decision")
    public ApiResponse<DecisionResponse> decision(
            @PathVariable Long id,
            @RequestParam String status) {
        DecisionRequest req = new DecisionRequest();
        req.setStatus(status);
        DecisionResponse response = adminService.makeDecision(id, req);
        return new ApiResponse<>(true, "Decision recorded.", response);
    }

    @GetMapping("/applications")
    public java.util.List<com.capgemini.adminservice.dto.ApplicationDTO> getAllApplications() {
        return adminService.getAllApplications();
    }

    @GetMapping("/stats")
    public ApiResponse<java.util.Map<String, Object>> getStats() {
        return new ApiResponse<>(true, "Stats fetched.", adminService.getStats());
    }

    @PostMapping("/lock-user")
    public ApiResponse<String> lockUser(@RequestParam String email) {
        adminService.lockUser(email);
        return new ApiResponse<>(true, "User locked.", "Success");
    }

    @GetMapping("/audit-logs")
    public ApiResponse<java.util.List<String>> getAuditLogs() {
        return new ApiResponse<>(true, "Audit logs fetched.", adminService.getAuditLogs());
    }
}
