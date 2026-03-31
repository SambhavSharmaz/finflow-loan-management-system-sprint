package com.capgemini.adminservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DecisionRequest {

    @NotBlank(message = "Status is required.")
    private String status;

    @Size(max = 255, message = "Remarks must be at most 255 characters.")
    private String remarks;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
