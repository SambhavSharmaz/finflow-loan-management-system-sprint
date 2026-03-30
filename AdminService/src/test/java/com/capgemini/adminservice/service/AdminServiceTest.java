package com.capgemini.adminservice.service;

import com.capgemini.adminservice.client.ApplicationClient;
import com.capgemini.adminservice.dto.DecisionRequest;
import com.capgemini.adminservice.dto.DecisionResponse;
import com.capgemini.adminservice.entity.Decision;
import com.capgemini.adminservice.entity.DecisionStatus;
import com.capgemini.adminservice.repository.DecisionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private DecisionRepository decisionRepository;

    @Mock
    private ApplicationClient applicationClient;

    @InjectMocks
    private AdminService adminService;

    @Test
    void makeDecisionShouldSaveDecisionAndUpdateApplication() {
        DecisionRequest request = new DecisionRequest();
        request.setStatus("approved");
        request.setRemarks("All checks passed");

        Decision savedDecision = new Decision();
        savedDecision.setId(5L);
        savedDecision.setApplicationId(1L);
        savedDecision.setStatus(DecisionStatus.APPROVED);
        savedDecision.setRemarks("All checks passed");

        when(decisionRepository.save(any(Decision.class))).thenReturn(savedDecision);

        DecisionResponse response = adminService.makeDecision(1L, request);

        ArgumentCaptor<Decision> decisionCaptor = ArgumentCaptor.forClass(Decision.class);
        verify(decisionRepository).save(decisionCaptor.capture());
        verify(applicationClient).updateStatus(1L, "APPROVED");

        Decision decision = decisionCaptor.getValue();
        assertEquals(1L, decision.getApplicationId());
        assertEquals(DecisionStatus.APPROVED, decision.getStatus());
        assertEquals("All checks passed", decision.getRemarks());

        assertEquals(5L, response.getId());
        assertEquals(DecisionStatus.APPROVED, response.getStatus());
    }

    @Test
    void makeDecisionShouldThrowWhenStatusIsInvalid() {
        DecisionRequest request = new DecisionRequest();
        request.setStatus("pending");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adminService.makeDecision(1L, request)
        );

        assertEquals("Invalid decision status.", exception.getMessage());
        verify(decisionRepository, never()).save(any(Decision.class));
        verify(applicationClient, never()).updateStatus(any(Long.class), any(String.class));
    }
}
