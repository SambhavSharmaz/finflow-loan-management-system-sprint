package com.capgemini.applicationservice.controller;

import com.capgemini.applicationservice.dto.ApiResponse;
import com.capgemini.applicationservice.dto.ApplicationRequest;
import com.capgemini.applicationservice.dto.ApplicationResponse;
import com.capgemini.applicationservice.entity.LoanApplication;
import com.capgemini.applicationservice.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    private String getUserEmail(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String[] split = token.split("\\.");
                if (split.length > 1) {
                    String payload = new String(java.util.Base64.getUrlDecoder().decode(split[1]));
                    return payload.split("\"sub\":\"")[1].split("\"")[0];
                }
            } catch (Exception e) {
                // ignore and fallback
            }
        }
        return "test@gmail.com";
    }

    @PostMapping
    public ApiResponse<ApplicationResponse> create(
            @org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody ApplicationRequest request) {
        String email = getUserEmail(authHeader);
        LoanApplication loanApplication = applicationService.create(request, email);
        ApplicationResponse response = toResponse(loanApplication);
        return new ApiResponse<>(true, "Application created successfully.", response);
    }

    @PostMapping("/{id}/submit")
    public ApiResponse<ApplicationResponse> submit(@PathVariable Long id) {
        LoanApplication loanApplication = applicationService.submit(id);
        ApplicationResponse response = toResponse(loanApplication);
        return new ApiResponse<>(true, "Application submitted.", response);
    }

    @GetMapping("/my")
    public ApiResponse<List<ApplicationResponse>> myApps(
            @org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String authHeader) {
        String email = getUserEmail(authHeader);
        List<LoanApplication> loanApplications = applicationService.getMyApps(email);
        List<ApplicationResponse> responses = new java.util.ArrayList<>();
        for (LoanApplication loanApplication : loanApplications) {
            responses.add(toResponse(loanApplication));
        }
        return new ApiResponse<>(true, "Applications fetched.", responses);
    }

    @GetMapping("/all")
    public List<ApplicationResponse> allApps() {
        List<LoanApplication> loanApplications = applicationService.getAllApps();
        List<ApplicationResponse> responses = new java.util.ArrayList<>();
        for (LoanApplication loanApplication : loanApplications) {
            responses.add(toResponse(loanApplication));
        }
        return responses;
    }

    @GetMapping("/{id}/status")
    public ApiResponse<String> status(@PathVariable Long id) {
        String status = applicationService.getStatus(id).name();
        return new ApiResponse<>(true, "Status fetched.", status);
    }

    @PutMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id, @RequestParam String status) {
        applicationService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return new ApiResponse<>(true, "Application deleted successfully.", "Success");
    }

    @PutMapping("/{id}")
    public ApiResponse<ApplicationResponse> update(@PathVariable Long id, @Valid @RequestBody ApplicationRequest request) {
        LoanApplication loanApplication = applicationService.update(id, request);
        ApplicationResponse response = toResponse(loanApplication);
        return new ApiResponse<>(true, "Application updated successfully.", response);
    }

    @GetMapping("/count")
    public ApiResponse<Long> count() {
        long count = applicationService.count();
        return new ApiResponse<>(true, "Total applications fetched.", count);
    }

    private ApplicationResponse toResponse(LoanApplication loanApplication) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(loanApplication.getId());
        response.setFullName(loanApplication.getFullName());
        response.setPhone(loanApplication.getPhone());
        response.setCompany(loanApplication.getCompany());
        response.setSalary(loanApplication.getSalary());
        response.setAmount(loanApplication.getAmount());
        response.setTenure(loanApplication.getTenure());
        response.setPurpose(loanApplication.getPurpose());
        response.setStatus(loanApplication.getStatus());
        return response;
    }
}
