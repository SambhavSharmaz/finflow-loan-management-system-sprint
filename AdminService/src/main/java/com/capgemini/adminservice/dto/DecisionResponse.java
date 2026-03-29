package com.capgemini.adminservice.dto;

import com.capgemini.adminservice.entity.DecisionStatus;

public class DecisionResponse {

    private Long id;
    private Long applicationId;
    private DecisionStatus status;
    private String remarks;

    public DecisionResponse() {
    }

    public DecisionResponse(Long id, Long applicationId, DecisionStatus status, String remarks) {
        this.id = id;
        this.applicationId = applicationId;
        this.status = status;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public DecisionStatus getStatus() {
        return status;
    }

    public void setStatus(DecisionStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
