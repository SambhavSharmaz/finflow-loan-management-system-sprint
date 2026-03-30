package com.capgemini.applicationservice.service;

import com.capgemini.applicationservice.dto.ApplicationRequest;
import com.capgemini.applicationservice.entity.LoanApplication;
import com.capgemini.applicationservice.entity.Status;
import com.capgemini.applicationservice.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void createShouldSaveDraftApplication() {
        ApplicationRequest request = new ApplicationRequest();
        request.setFullName("Rahul Sharma");
        request.setPhone("9876543210");
        request.setCompany("ABC Pvt Ltd");
        request.setSalary(50000.0);
        request.setAmount(200000.0);
        request.setTenure(24);
        request.setPurpose("Home renovation");

        when(loanRepository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanApplication savedApplication = applicationService.create(request, "user@example.com");

        ArgumentCaptor<LoanApplication> applicationCaptor = ArgumentCaptor.forClass(LoanApplication.class);
        verify(loanRepository).save(applicationCaptor.capture());

        LoanApplication application = applicationCaptor.getValue();
        assertEquals("user@example.com", application.getUserEmail());
        assertEquals("Rahul Sharma", application.getFullName());
        assertEquals(Status.DRAFT, application.getStatus());
        assertEquals("Home renovation", savedApplication.getPurpose());
    }

    @Test
    void submitShouldThrowWhenApplicationDoesNotExist() {
        when(loanRepository.findById(10L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> applicationService.submit(10L)
        );

        assertEquals("Application not found.", exception.getMessage());
    }

    @Test
    void updateStatusShouldThrowWhenStatusIsInvalid() {
        LoanApplication application = new LoanApplication();
        application.setId(1L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(application));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.updateStatus(1L, "wrong-status")
        );

        assertEquals("Invalid application status.", exception.getMessage());
        verify(loanRepository, never()).save(any(LoanApplication.class));
    }
}
