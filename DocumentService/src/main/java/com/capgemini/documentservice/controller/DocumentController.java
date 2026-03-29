package com.capgemini.documentservice.controller;

import com.capgemini.documentservice.dto.ApiResponse;
import com.capgemini.documentservice.dto.DocumentResponse;
import com.capgemini.documentservice.service.DocumentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ApiResponse<DocumentResponse> upload(
            @RequestParam Long applicationId,
            @RequestParam MultipartFile file) {

        DocumentResponse response = documentService.upload(applicationId, file);
        return new ApiResponse<>(true, "Document uploaded.", response);
    }

    @PutMapping("/{id}/verify")
    public ApiResponse<DocumentResponse> verify(@PathVariable Long id) {
        DocumentResponse response = documentService.verify(id);
        return new ApiResponse<>(true, "Document verified.", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<DocumentResponse> get(@PathVariable Long id) {
        DocumentResponse response = documentService.get(id);
        return new ApiResponse<>(true, "Document fetched.", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        documentService.delete(id);
        return new ApiResponse<>(true, "Document deleted.", "Success");
    }

    @GetMapping("/application/{applicationId}")
    public ApiResponse<List<DocumentResponse>> getByApplicationId(@PathVariable Long applicationId) {
        List<DocumentResponse> responses = documentService.getByApplicationId(applicationId);
        return new ApiResponse<>(true, "Documents fetched.", responses);
    }
}
