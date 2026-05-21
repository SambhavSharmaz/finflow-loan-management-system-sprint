package com.capgemini.adminservice.controller;

import com.capgemini.adminservice.dto.ApiResponse;
import com.capgemini.adminservice.dto.ApplicationDTO;
import com.capgemini.adminservice.dto.DecisionResponse;
import com.capgemini.adminservice.dto.ReportResponse;
import com.capgemini.adminservice.dto.UserDTO;
import com.capgemini.adminservice.entity.DecisionStatus;
import com.capgemini.adminservice.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void decision_Success() {
        DecisionResponse decisionResponse = new DecisionResponse();
        decisionResponse.setId(1L);
        decisionResponse.setApplicationId(100L);
        decisionResponse.setStatus(DecisionStatus.APPROVED);

        when(adminService.makeDecision(eq(100L), any())).thenReturn(decisionResponse);

        ApiResponse<DecisionResponse> response = adminController.decision(100L, "APPROVED");

        assertTrue(response.isSuccess());
        assertEquals("Decision recorded.", response.getMessage());
        assertEquals(DecisionStatus.APPROVED, response.getData().getStatus());
    }

    @Test
    void getAllApplications_Success() {
        ApplicationDTO app = new ApplicationDTO();
        app.setId(1L);
        app.setFullName("John");

        when(adminService.getAllApplications()).thenReturn(List.of(app));

        List<ApplicationDTO> result = adminController.getAllApplications();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFullName());
    }

    @Test
    void getStats_Success() {
        Map<String, Object> stats = Map.of("totalApplications", 10L, "approvedLoans", 5L);
        when(adminService.getStats()).thenReturn(stats);

        ApiResponse<Map<String, Object>> response = adminController.getStats();

        assertTrue(response.isSuccess());
        assertEquals(10L, response.getData().get("totalApplications"));
    }

    @Test
    void generateReport_Success() {
        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setId(1L);
        reportResponse.setTitle("Loan Applications Report");
        reportResponse.setTotalApplications(10L);
        reportResponse.setApprovedCount(5L);

        when(adminService.generateReport()).thenReturn(reportResponse);

        ApiResponse<ReportResponse> response = adminController.generateReport();

        assertTrue(response.isSuccess());
        assertEquals("Report generated.", response.getMessage());
        assertEquals(10L, response.getData().getTotalApplications());
    }

    @Test
    void getReports_Success() {
        ReportResponse r1 = new ReportResponse();
        r1.setId(1L);
        r1.setTitle("Report 1");

        when(adminService.getReportHistory()).thenReturn(List.of(r1));

        ApiResponse<List<ReportResponse>> response = adminController.getReports();

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    void getAllUsers_Success() {
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setName("John");
        user1.setRole("ROLE_USER");

        when(adminService.getAllUsers()).thenReturn(List.of(user1));

        ApiResponse<List<UserDTO>> response = adminController.getAllUsers();

        assertTrue(response.isSuccess());
        assertEquals("Users fetched.", response.getMessage());
        assertEquals(1, response.getData().size());
        assertEquals("John", response.getData().get(0).getName());
    }

    @Test
    void updateUser_Success() {
        UserDTO updated = new UserDTO();
        updated.setId(1L);
        updated.setName("John");
        updated.setRole("ROLE_ADMIN");

        when(adminService.updateUserRole(1L, "ROLE_ADMIN")).thenReturn(updated);

        ApiResponse<UserDTO> response = adminController.updateUser(1L, "ROLE_ADMIN");

        assertTrue(response.isSuccess());
        assertEquals("User updated.", response.getMessage());
        assertEquals("ROLE_ADMIN", response.getData().getRole());
    }

    @Test
    void lockUser_Success() {
        doNothing().when(adminService).lockUser("test@gmail.com");

        ApiResponse<String> response = adminController.lockUser("test@gmail.com");

        assertTrue(response.isSuccess());
        assertEquals("User locked.", response.getMessage());
        verify(adminService).lockUser("test@gmail.com");
    }

    @Test
    void getAuditLogs_Success() {
        List<String> logs = List.of("Log 1", "Log 2");
        when(adminService.getAuditLogs()).thenReturn(logs);

        ApiResponse<List<String>> response = adminController.getAuditLogs();

        assertTrue(response.isSuccess());
        assertEquals(2, response.getData().size());
    }
}
