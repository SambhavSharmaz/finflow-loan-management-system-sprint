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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class AdminService {

    private final DecisionRepository decisionRepository;
    private final ReportRepository reportRepository;
    private final ApplicationClient applicationClient;
    private final AuthClient authClient;

    public AdminService(
            DecisionRepository decisionRepository,
            ReportRepository reportRepository,
            ApplicationClient applicationClient,
            AuthClient authClient) {
        this.decisionRepository = decisionRepository;
        this.reportRepository = reportRepository;
        this.applicationClient = applicationClient;
        this.authClient = authClient;
    }

    public DecisionResponse makeDecision(Long applicationId, DecisionRequest request) {
        DecisionStatus decisionStatus = parseStatus(request.getStatus());
        Decision decision = new Decision();
        decision.setApplicationId(applicationId);
        decision.setStatus(decisionStatus);
        decision.setRemarks(request.getRemarks());
        Decision savedDecision = decisionRepository.save(decision);
        applicationClient.updateStatus(applicationId, decisionStatus.name());
        return map(savedDecision);
    }

    public List<ApplicationDTO> getAllApplications() {
        return applicationClient.getAllApplications();
    }

    public Map<String, Object> getStats() {
        List<ApplicationDTO> apps = applicationClient.getAllApplications();
        List<UserDTO> users = authClient.getAllUsers();
        long totalDecisions = decisionRepository.count();

        long approved = 0;
        long rejected = 0;
        long pending = 0;
        double totalAmount = 0;

        for (ApplicationDTO app : apps) {
            String status = app.getStatus();
            if (app.getAmount() != null) {
                totalAmount += app.getAmount();
            }
            if ("APPROVED".equals(status)) {
                approved++;
            } else if ("REJECTED".equals(status)) {
                rejected++;
            } else if (status != null && !status.equals("CLOSED")) {
                pending++;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", (long) users.size());
        stats.put("totalApplications", (long) apps.size());
        stats.put("approvedLoans", approved);
        stats.put("rejectedLoans", rejected);
        stats.put("pendingReview", pending);
        stats.put("totalDecisions", totalDecisions);
        stats.put("totalLoanAmount", totalAmount);
        return stats;
    }

    public ReportResponse generateReport() {
        List<ApplicationDTO> apps = applicationClient.getAllApplications();

        // Compute status breakdown
        Map<String, Long> statusBreakdown = new HashMap<>();
        long approved = 0;
        long rejected = 0;
        long pending = 0;
        double totalAmount = 0;
        double approvedAmount = 0;

        for (ApplicationDTO app : apps) {
            String status = app.getStatus() != null ? app.getStatus() : "UNKNOWN";
            statusBreakdown.merge(status, 1L, Long::sum);

            if (app.getAmount() != null) {
                totalAmount += app.getAmount();
            }

            if ("APPROVED".equals(status)) {
                approved++;
                if (app.getAmount() != null) {
                    approvedAmount += app.getAmount();
                }
            } else if ("REJECTED".equals(status)) {
                rejected++;
            } else if (!Set.of("CLOSED", "UNKNOWN").contains(status)) {
                pending++;
            }
        }

        // Persist the report
        Report report = new Report();
        report.setTitle("Loan Applications Report");
        report.setSummary("Generated report with " + apps.size() + " total applications, " +
                approved + " approved, " + rejected + " rejected, " + pending + " pending.");
        report.setTotalApplications((long) apps.size());
        report.setApprovedCount(approved);
        report.setRejectedCount(rejected);
        report.setPendingCount(pending);
        report.setTotalLoanAmount(totalAmount);
        report.setApprovedLoanAmount(approvedAmount);
        report.setGeneratedAt(LocalDateTime.now());
        Report savedReport = reportRepository.save(report);

        // Build response
        ReportResponse response = new ReportResponse();
        response.setId(savedReport.getId());
        response.setTitle(savedReport.getTitle());
        response.setSummary(savedReport.getSummary());
        response.setTotalApplications(savedReport.getTotalApplications());
        response.setApprovedCount(savedReport.getApprovedCount());
        response.setRejectedCount(savedReport.getRejectedCount());
        response.setPendingCount(savedReport.getPendingCount());
        response.setTotalLoanAmount(savedReport.getTotalLoanAmount());
        response.setApprovedLoanAmount(savedReport.getApprovedLoanAmount());
        response.setGeneratedAt(savedReport.getGeneratedAt());
        response.setStatusBreakdown(statusBreakdown);
        return response;
    }

    public List<ReportResponse> getReportHistory() {
        List<Report> reports = reportRepository.findAllByOrderByGeneratedAtDesc();
        List<ReportResponse> responses = new java.util.ArrayList<>();
        for (Report r : reports) {
            ReportResponse res = new ReportResponse();
            res.setId(r.getId());
            res.setTitle(r.getTitle());
            res.setSummary(r.getSummary());
            res.setTotalApplications(r.getTotalApplications());
            res.setApprovedCount(r.getApprovedCount());
            res.setRejectedCount(r.getRejectedCount());
            res.setPendingCount(r.getPendingCount());
            res.setTotalLoanAmount(r.getTotalLoanAmount());
            res.setApprovedLoanAmount(r.getApprovedLoanAmount());
            res.setGeneratedAt(r.getGeneratedAt());
            responses.add(res);
        }
        return responses;
    }

    // ----- User Management -----

    public List<UserDTO> getAllUsers() {
        return authClient.getAllUsers();
    }

    public UserDTO updateUserRole(Long userId, String role) {
        return authClient.updateUser(userId, role);
    }



    private DecisionResponse map(Decision decision) {
        DecisionResponse response = new DecisionResponse();
        response.setId(decision.getId());
        response.setApplicationId(decision.getApplicationId());
        response.setStatus(decision.getStatus());
        response.setRemarks(decision.getRemarks());
        return response;
    }

    private DecisionStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Decision status is required.");
        }

        try {
            return DecisionStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid decision status.");
        }
    }
}
