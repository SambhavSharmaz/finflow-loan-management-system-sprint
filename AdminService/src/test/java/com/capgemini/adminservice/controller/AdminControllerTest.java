package com.capgemini.adminservice.controller;

import com.capgemini.adminservice.dto.ApiResponse;
import com.capgemini.adminservice.dto.ApplicationDTO;
import com.capgemini.adminservice.dto.DecisionResponse;
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
        Map<String, Object> stats = Map.of("totalUsers", 100, "activeLoans", 42);
        when(adminService.getStats()).thenReturn(stats);

        ApiResponse<Map<String, Object>> response = adminController.getStats();

        assertTrue(response.isSuccess());
        assertEquals(100, response.getData().get("totalUsers"));
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
