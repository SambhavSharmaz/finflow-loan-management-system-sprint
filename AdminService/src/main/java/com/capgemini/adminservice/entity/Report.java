package com.capgemini.adminservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private Long totalApplications;
    private Long approvedCount;
    private Long rejectedCount;
    private Long pendingCount;
    private Double totalLoanAmount;
    private Double approvedLoanAmount;
    private LocalDateTime generatedAt;

    public Report() {
        this.generatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(Long totalApplications) { this.totalApplications = totalApplications; }

    public Long getApprovedCount() { return approvedCount; }
    public void setApprovedCount(Long approvedCount) { this.approvedCount = approvedCount; }

    public Long getRejectedCount() { return rejectedCount; }
    public void setRejectedCount(Long rejectedCount) { this.rejectedCount = rejectedCount; }

    public Long getPendingCount() { return pendingCount; }
    public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }

    public Double getTotalLoanAmount() { return totalLoanAmount; }
    public void setTotalLoanAmount(Double totalLoanAmount) { this.totalLoanAmount = totalLoanAmount; }

    public Double getApprovedLoanAmount() { return approvedLoanAmount; }
    public void setApprovedLoanAmount(Double approvedLoanAmount) { this.approvedLoanAmount = approvedLoanAmount; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
