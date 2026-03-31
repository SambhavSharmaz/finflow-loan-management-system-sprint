package com.capgemini.applicationservice.service;

import com.capgemini.applicationservice.dto.ApplicationRequest;
import com.capgemini.applicationservice.entity.LoanApplication;
import com.capgemini.applicationservice.entity.Status;
import com.capgemini.applicationservice.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private ApplicationService applicationService;

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

    @Test
    void create_Success() {
        ApplicationRequest request = createRequest();
        LoanApplication savedApp = createLoanApplication();

        when(loanRepository.save(any(LoanApplication.class))).thenReturn(savedApp);

        LoanApplication result = applicationService.create(request, "john@test.com");

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals(Status.DRAFT, result.getStatus());
        verify(loanRepository).save(any(LoanApplication.class));
    }

    @Test
    void submit_Success() {
        LoanApplication app = createLoanApplication();
        LoanApplication submittedApp = createLoanApplication();
        submittedApp.setStatus(Status.SUBMITTED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(app));
        when(loanRepository.save(any(LoanApplication.class))).thenReturn(submittedApp);

        LoanApplication result = applicationService.submit(1L);

        assertEquals(Status.SUBMITTED, result.getStatus());
    }

    @Test
    void submit_NotFound() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> applicationService.submit(999L));
    }

    @Test
    void getMyApps_Success() {
        LoanApplication app = createLoanApplication();
        when(loanRepository.findByUserEmail("john@test.com")).thenReturn(List.of(app));

        List<LoanApplication> result = applicationService.getMyApps("john@test.com");

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
    }

    @Test
    void getAllApps_Success() {
        LoanApplication app1 = createLoanApplication();
        LoanApplication app2 = createLoanApplication();
        app2.setId(2L);
        app2.setFullName("Jane Doe");

        when(loanRepository.findAll()).thenReturn(List.of(app1, app2));

        List<LoanApplication> result = applicationService.getAllApps();

        assertEquals(2, result.size());
    }

    @Test
    void getStatus_Success() {
        LoanApplication app = createLoanApplication();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(app));

        Status status = applicationService.getStatus(1L);

        assertEquals(Status.DRAFT, status);
    }

    @Test
    void getStatus_NotFound() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> applicationService.getStatus(999L));
    }

    @Test
    void updateStatus_Success() {
        LoanApplication app = createLoanApplication();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(app));
        when(loanRepository.save(any(LoanApplication.class))).thenReturn(app);

        assertDoesNotThrow(() -> applicationService.updateStatus(1L, "APPROVED"));
        verify(loanRepository).save(any(LoanApplication.class));
    }

    @Test
    void updateStatus_InvalidStatus() {
        LoanApplication app = createLoanApplication();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(app));

        assertThrows(IllegalArgumentException.class,
                () -> applicationService.updateStatus(1L, "INVALID"));
    }

    @Test
    void updateStatus_NullStatus() {
        LoanApplication app = createLoanApplication();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(app));

        assertThrows(IllegalArgumentException.class,
                () -> applicationService.updateStatus(1L, null));
    }

    @Test
    void delete_Success() {
        LoanApplication app = createLoanApplication();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(app));
        doNothing().when(loanRepository).delete(app);

        assertDoesNotThrow(() -> applicationService.delete(1L));
        verify(loanRepository).delete(app);
    }

    @Test
    void delete_NotFound() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> applicationService.delete(999L));
    }

    @Test
    void update_Success() {
        ApplicationRequest request = createRequest();
        request.setFullName("Updated Name");

        LoanApplication existingApp = createLoanApplication();
        LoanApplication updatedApp = createLoanApplication();
        updatedApp.setFullName("Updated Name");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(existingApp));
        when(loanRepository.save(any(LoanApplication.class))).thenReturn(updatedApp);

        LoanApplication result = applicationService.update(1L, request);

        assertEquals("Updated Name", result.getFullName());
    }

    @Test
    void count_Success() {
        when(loanRepository.count()).thenReturn(5L);

        long count = applicationService.count();

        assertEquals(5L, count);
    }
}
