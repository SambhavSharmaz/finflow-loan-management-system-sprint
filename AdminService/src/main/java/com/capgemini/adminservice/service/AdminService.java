package com.capgemini.adminservice.service;

import com.capgemini.adminservice.client.ApplicationClient;
import com.capgemini.adminservice.dto.DecisionRequest;
import com.capgemini.adminservice.dto.DecisionResponse;
import com.capgemini.adminservice.entity.Decision;
import com.capgemini.adminservice.entity.DecisionStatus;
import com.capgemini.adminservice.repository.DecisionRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AdminService {

    private DecisionRepository decisionRepository;
    private ApplicationClient applicationClient;

    public AdminService(
            DecisionRepository decisionRepository,
            ApplicationClient applicationClient) {
        this.decisionRepository = decisionRepository;
        this.applicationClient = applicationClient;
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

    public java.util.List<com.capgemini.adminservice.dto.ApplicationDTO> getAllApplications() {
        return applicationClient.getAllApplications();
    }

    public java.util.Map<String, Object> getStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUsers", 100);
        stats.put("activeLoans", 42);
        stats.put("pendingApprovals", 15);
        return stats;
    }

    public void lockUser(String email) {
        // dummy implementation
    }

    public java.util.List<String> getAuditLogs() {
        java.util.List<String> logs = new java.util.ArrayList<>();
        logs.add("User test@gmail.com logged in");
        logs.add("Admin approved application #1");
        logs.add("Document verified for application #2");
        return logs;
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
