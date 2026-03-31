package com.capgemini.adminservice.service;

import com.capgemini.adminservice.client.ApplicationClient;
import com.capgemini.adminservice.dto.ApplicationDTO;
import com.capgemini.adminservice.dto.DecisionRequest;
import com.capgemini.adminservice.dto.DecisionResponse;
import com.capgemini.adminservice.entity.Decision;
import com.capgemini.adminservice.entity.DecisionStatus;
import com.capgemini.adminservice.repository.DecisionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private DecisionRepository decisionRepository;

    @Mock
    private ApplicationClient applicationClient;

    @InjectMocks
    private AdminService adminService;

    @Test
    void makeDecision_Approved() {
        DecisionRequest request = new DecisionRequest();
        request.setStatus("APPROVED");
        request.setRemarks("Looks good");

        Decision savedDecision = new Decision();
        savedDecision.setId(1L);
        savedDecision.setApplicationId(100L);
        savedDecision.setStatus(DecisionStatus.APPROVED);
        savedDecision.setRemarks("Looks good");

        when(decisionRepository.save(any(Decision.class))).thenReturn(savedDecision);
        doNothing().when(applicationClient).updateStatus(100L, "APPROVED");

        DecisionResponse response = adminService.makeDecision(100L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100L, response.getApplicationId());
        assertEquals(DecisionStatus.APPROVED, response.getStatus());
        assertEquals("Looks good", response.getRemarks());
        verify(applicationClient).updateStatus(100L, "APPROVED");
    }

    @Test
    void makeDecision_Rejected() {
        DecisionRequest request = new DecisionRequest();
        request.setStatus("REJECTED");
        request.setRemarks("Insufficient income");

        Decision savedDecision = new Decision();
        savedDecision.setId(2L);
        savedDecision.setApplicationId(200L);
        savedDecision.setStatus(DecisionStatus.REJECTED);
        savedDecision.setRemarks("Insufficient income");

        when(decisionRepository.save(any(Decision.class))).thenReturn(savedDecision);
        doNothing().when(applicationClient).updateStatus(200L, "REJECTED");

        DecisionResponse response = adminService.makeDecision(200L, request);

        assertNotNull(response);
        assertEquals(DecisionStatus.REJECTED, response.getStatus());
    }

    @Test
    void makeDecision_InvalidStatus() {
        DecisionRequest request = new DecisionRequest();
        request.setStatus("INVALID");

        assertThrows(IllegalArgumentException.class,
                () -> adminService.makeDecision(100L, request));
    }

    @Test
    void makeDecision_NullStatus() {
        DecisionRequest request = new DecisionRequest();
        request.setStatus(null);

        assertThrows(IllegalArgumentException.class,
                () -> adminService.makeDecision(100L, request));
    }

    @Test
    void makeDecision_BlankStatus() {
        DecisionRequest request = new DecisionRequest();
        request.setStatus("   ");

        assertThrows(IllegalArgumentException.class,
                () -> adminService.makeDecision(100L, request));
    }

    @Test
    void getAllApplications_Success() {
        ApplicationDTO app1 = new ApplicationDTO();
        app1.setId(1L);
        app1.setFullName("John Doe");

        ApplicationDTO app2 = new ApplicationDTO();
        app2.setId(2L);
        app2.setFullName("Jane Doe");

        when(applicationClient.getAllApplications()).thenReturn(List.of(app1, app2));

        List<ApplicationDTO> result = adminService.getAllApplications();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
    }

    @Test
    void getStats_ReturnsStats() {
        Map<String, Object> stats = adminService.getStats();

        assertNotNull(stats);
        assertEquals(100, stats.get("totalUsers"));
        assertEquals(42, stats.get("activeLoans"));
        assertEquals(15, stats.get("pendingApprovals"));
    }

    @Test
    void lockUser_DoesNotThrow() {
        assertDoesNotThrow(() -> adminService.lockUser("test@gmail.com"));
    }

    @Test
    void getAuditLogs_ReturnsLogs() {
        List<String> logs = adminService.getAuditLogs();

        assertNotNull(logs);
        assertEquals(3, logs.size());
        assertTrue(logs.get(0).contains("logged in"));
    }
}
