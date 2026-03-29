package com.capgemini.documentservice.dto;

public class DocumentResponse {

    private Long id;
    private Long applicationId;
    private String fileName;
    private String fileUrl;
    private String status;

    public DocumentResponse() {
    }

    public DocumentResponse(Long id, Long applicationId, String fileName, String fileUrl, String status) {
        this.id = id;
        this.applicationId = applicationId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.status = status;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
