package com.capgemini.applicationservice.controller;


import com.capgemini.applicationservice.dto.ApiResponse;
import com.capgemini.applicationservice.dto.ApplicationRequest;
import com.capgemini.applicationservice.dto.ApplicationResponse;
import com.capgemini.applicationservice.entity.LoanApplication;
import com.capgemini.applicationservice.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService service;

    private String getUser() {
        return "test@gmail.com";
    }

    @PostMapping
    public ApiResponse<ApplicationResponse> create(@RequestBody ApplicationRequest request) {
        LoanApplication savedApplication = service.create(request, getUser());
        ApplicationResponse response = toResponse(savedApplication);
        ApiResponse<ApplicationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Application created successfully");
        apiResponse.setData(response);
        return apiResponse;
    }

    @PostMapping("/{id}/submit")
    public ApiResponse<ApplicationResponse> submit(@PathVariable Long id) {
        LoanApplication savedApplication = service.submit(id);
        ApplicationResponse response = toResponse(savedApplication);
        ApiResponse<ApplicationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Application submitted");
        apiResponse.setData(response);
        return apiResponse;
    }

    @GetMapping("/my")
    public ApiResponse<List<ApplicationResponse>> myApps() {
        List<LoanApplication> applications = service.getMyApps(getUser());
        List<ApplicationResponse> list = new ArrayList<>();
        for (LoanApplication application : applications) {
            list.add(toResponse(application));
        }
        ApiResponse<List<ApplicationResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Applications fetched");
        apiResponse.setData(list);
        return apiResponse;
    }

    @GetMapping("/{id}/status")
    public ApiResponse<String> status(@PathVariable Long id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Status fetched");
        apiResponse.setData(service.getStatus(id).name());
        return apiResponse;
    }

    private ApplicationResponse toResponse(LoanApplication application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setFullName(application.getFullName());
        response.setPhone(application.getPhone());
        response.setCompany(application.getCompany());
        response.setSalary(application.getSalary());
        response.setAmount(application.getAmount());
        response.setTenure(application.getTenure());
        response.setStatus(application.getStatus());
        return response;
    }
}
