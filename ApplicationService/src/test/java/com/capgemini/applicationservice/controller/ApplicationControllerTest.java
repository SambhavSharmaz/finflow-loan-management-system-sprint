package com.capgemini.applicationservice.controller;

import com.capgemini.applicationservice.dto.ApiResponse;
import com.capgemini.applicationservice.dto.ApplicationRequest;
import com.capgemini.applicationservice.dto.ApplicationResponse;
import com.capgemini.applicationservice.entity.LoanApplication;
import com.capgemini.applicationservice.entity.Status;
import com.capgemini.applicationservice.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    private LoanApplication createLoanApplication() {
        LoanApplication app = new LoanApplication();
        app.setId(1L);
        app.setUserEmail("john@test.com");
        app.setFullName("John Doe");
        app.setPhone("1234567890");
        app.setCompany("Capgemini");
        app.setSalary(50000.0);
        app.setAmount(200000.0);
        app.setTenure(12);
        app.setPurpose("Home Loan");
        app.setStatus(Status.DRAFT);
        return app;
    }

    private ApplicationRequest createRequest() {
        ApplicationRequest request = new ApplicationRequest();
        request.setFullName("John Doe");
        request.setPhone("1234567890");
        request.setCompany("Capgemini");
        request.setSalary(50000.0);
        request.setAmount(200000.0);
        request.setTenure(12);
        request.setPurpose("Home Loan");
        return request;
    }

    @Test
    void create_Success() {
        ApplicationRequest request = createRequest();
        LoanApplication app = createLoanApplication();

        when(applicationService.create(any(ApplicationRequest.class), eq("test@gmail.com")))
                .thenReturn(app);

        ApiResponse<ApplicationResponse> response = applicationController.create(null, request);

        assertTrue(response.isSuccess());
        assertEquals("Application created successfully.", response.getMessage());
        assertEquals("John Doe", response.getData().getFullName());
    }

    @Test
    void submit_Success() {
        LoanApplication app = createLoanApplication();
        app.setStatus(Status.SUBMITTED);

        when(applicationService.submit(1L)).thenReturn(app);

        ApiResponse<ApplicationResponse> response = applicationController.submit(1L);

        assertTrue(response.isSuccess());
        assertEquals("Application submitted.", response.getMessage());
        assertEquals(Status.SUBMITTED, response.getData().getStatus());
    }

    @Test
    void myApps_Success() {
        LoanApplication app = createLoanApplication();
        when(applicationService.getMyApps("test@gmail.com")).thenReturn(List.of(app));

        ApiResponse<List<ApplicationResponse>> response = applicationController.myApps(null);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    void allApps_Success() {
        LoanApplication app1 = createLoanApplication();
        LoanApplication app2 = createLoanApplication();
        app2.setId(2L);

        when(applicationService.getAllApps()).thenReturn(List.of(app1, app2));

        List<ApplicationResponse> result = applicationController.allApps();

        assertEquals(2, result.size());
    }

    @Test
    void status_Success() {
        when(applicationService.getStatus(1L)).thenReturn(Status.DRAFT);

        ApiResponse<String> response = applicationController.status(1L);

        assertTrue(response.isSuccess());
        assertEquals("DRAFT", response.getData());
    }

    @Test
    void updateStatus_Success() {
        doNothing().when(applicationService).updateStatus(1L, "APPROVED");

        assertDoesNotThrow(() -> applicationController.updateStatus(1L, "APPROVED"));
        verify(applicationService).updateStatus(1L, "APPROVED");
    }

    @Test
    void delete_Success() {
        doNothing().when(applicationService).delete(1L);

        ApiResponse<String> response = applicationController.delete(1L);

        assertTrue(response.isSuccess());
        assertEquals("Application deleted successfully.", response.getMessage());
    }

    @Test
    void update_Success() {
        ApplicationRequest request = createRequest();
        LoanApplication app = createLoanApplication();

        when(applicationService.update(eq(1L), any(ApplicationRequest.class))).thenReturn(app);

        ApiResponse<ApplicationResponse> response = applicationController.update(1L, request);

        assertTrue(response.isSuccess());
        assertEquals("Application updated successfully.", response.getMessage());
    }

    @Test
    void count_Success() {
        when(applicationService.count()).thenReturn(5L);

        ApiResponse<Long> response = applicationController.count();

        assertTrue(response.isSuccess());
        assertEquals(5L, response.getData());
    }
}
