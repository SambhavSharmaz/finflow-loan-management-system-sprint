package com.capgemini.adminservice.service;

import com.capgemini.adminservice.client.ApplicationClient;
import com.capgemini.adminservice.client.AuthClient;
import com.capgemini.adminservice.dto.ApplicationDTO;
import com.capgemini.adminservice.dto.DecisionRequest;
import com.capgemini.adminservice.dto.DecisionResponse;
import com.capgemini.adminservice.dto.ReportResponse;
import com.capgemini.adminservice.dto.UserDTO;
import com.capgemini.adminservice.entity.Decision;
import com.capgemini.adminservice.entity.DecisionStatus;
import com.capgemini.adminservice.entity.Report;
import com.capgemini.adminservice.repository.DecisionRepository;
import com.capgemini.adminservice.repository.ReportRepository;
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
    private ReportRepository reportRepository;

    @Mock
    private ApplicationClient applicationClient;

    @Mock
    private AuthClient authClient;

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
    void getStats_ReturnsRealStats() {
        ApplicationDTO app1 = new ApplicationDTO();
        app1.setId(1L);
        app1.setStatus("APPROVED");
        app1.setAmount(100000.0);

        ApplicationDTO app2 = new ApplicationDTO();
        app2.setId(2L);
        app2.setStatus("REJECTED");
        app2.setAmount(50000.0);

        ApplicationDTO app3 = new ApplicationDTO();
        app3.setId(3L);
        app3.setStatus("SUBMITTED");
        app3.setAmount(75000.0);

        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setName("John");

        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setName("Jane");

        when(applicationClient.getAllApplications()).thenReturn(List.of(app1, app2, app3));
        when(authClient.getAllUsers()).thenReturn(List.of(user1, user2));
        when(decisionRepository.count()).thenReturn(2L);

        Map<String, Object> stats = adminService.getStats();

        assertNotNull(stats);
        assertEquals(3L, stats.get("totalApplications"));
        assertEquals(1L, stats.get("approvedLoans"));
        assertEquals(1L, stats.get("rejectedLoans"));
        assertEquals(1L, stats.get("pendingReview"));
        assertEquals(2L, stats.get("totalDecisions"));
        assertEquals(225000.0, stats.get("totalLoanAmount"));
        assertEquals(2L, stats.get("totalUsers"));
    }

    @Test
    void generateReport_Success() {
        ApplicationDTO app1 = new ApplicationDTO();
        app1.setId(1L);
        app1.setStatus("APPROVED");
        app1.setAmount(200000.0);

        ApplicationDTO app2 = new ApplicationDTO();
        app2.setId(2L);
        app2.setStatus("SUBMITTED");
        app2.setAmount(100000.0);

        when(applicationClient.getAllApplications()).thenReturn(List.of(app1, app2));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        ReportResponse response = adminService.generateReport();

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getTotalApplications());
        assertEquals(1L, response.getApprovedCount());
        assertEquals(0L, response.getRejectedCount());
        assertEquals(1L, response.getPendingCount());
        assertEquals(300000.0, response.getTotalLoanAmount());
        assertEquals(200000.0, response.getApprovedLoanAmount());
        assertNotNull(response.getStatusBreakdown());
        assertEquals(1L, response.getStatusBreakdown().get("APPROVED"));
        assertEquals(1L, response.getStatusBreakdown().get("SUBMITTED"));
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void getReportHistory_Success() {
        Report r1 = new Report();
        r1.setId(1L);
        r1.setTitle("Report 1");
        r1.setTotalApplications(10L);
        r1.setApprovedCount(5L);
        r1.setRejectedCount(2L);
        r1.setPendingCount(3L);
        r1.setTotalLoanAmount(500000.0);
        r1.setApprovedLoanAmount(250000.0);

        when(reportRepository.findAllByOrderByGeneratedAtDesc()).thenReturn(List.of(r1));

        List<ReportResponse> result = adminService.getReportHistory();

        assertEquals(1, result.size());
        assertEquals("Report 1", result.get(0).getTitle());
        assertEquals(10L, result.get(0).getTotalApplications());
    }

    @Test
    void getAllUsers_Success() {
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@test.com");
        user1.setRole("ROLE_USER");

        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setName("Admin");
        user2.setEmail("admin@test.com");
        user2.setRole("ROLE_ADMIN");

        when(authClient.getAllUsers()).thenReturn(List.of(user1, user2));

        List<UserDTO> result = adminService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getName());
        assertEquals("ROLE_ADMIN", result.get(1).getRole());
    }

    @Test
    void updateUserRole_Success() {
        UserDTO updated = new UserDTO();
        updated.setId(1L);
        updated.setName("John");
        updated.setRole("ROLE_ADMIN");

        when(authClient.updateUser(1L, "ROLE_ADMIN")).thenReturn(updated);

        UserDTO result = adminService.updateUserRole(1L, "ROLE_ADMIN");

        assertEquals("ROLE_ADMIN", result.getRole());
        verify(authClient).updateUser(1L, "ROLE_ADMIN");
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
