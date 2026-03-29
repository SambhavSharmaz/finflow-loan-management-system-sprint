package com.capgemini.applicationservice.service;

import com.capgemini.applicationservice.dto.ApplicationRequest;
import com.capgemini.applicationservice.entity.LoanApplication;
import com.capgemini.applicationservice.entity.Status;
import com.capgemini.applicationservice.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
public class ApplicationService {

    private LoanRepository loanRepository;

    public ApplicationService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public LoanApplication create(ApplicationRequest request, String email) {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setUserEmail(email);
        loanApplication.setFullName(request.getFullName());
        loanApplication.setPhone(request.getPhone());
        loanApplication.setCompany(request.getCompany());
        loanApplication.setSalary(request.getSalary());
        loanApplication.setAmount(request.getAmount());
        loanApplication.setTenure(request.getTenure());
        loanApplication.setPurpose(request.getPurpose());
        loanApplication.setStatus(Status.DRAFT);
        return loanRepository.save(loanApplication);
    }

    public LoanApplication submit(Long id) {
        LoanApplication loanApplication = findApplicationById(id);
        loanApplication.setStatus(Status.SUBMITTED);
        return loanRepository.save(loanApplication);
    }

    public List<LoanApplication> getMyApps(String email) {
        return loanRepository.findByUserEmail(email);
    }

    public List<LoanApplication> getAllApps() {
        return loanRepository.findAll();
    }

    public Status getStatus(Long id) {
        return findApplicationById(id).getStatus();
    }

    public void updateStatus(Long id, String status) {
        LoanApplication loanApplication = findApplicationById(id);
        loanApplication.setStatus(parseStatus(status));
        loanRepository.save(loanApplication);
    }

    public void delete(Long id) {
        LoanApplication loanApplication = findApplicationById(id);
        loanRepository.delete(loanApplication);
    }

    public LoanApplication update(Long id, ApplicationRequest request) {
        LoanApplication loanApplication = findApplicationById(id);
        loanApplication.setFullName(request.getFullName());
        loanApplication.setPhone(request.getPhone());
        loanApplication.setCompany(request.getCompany());
        loanApplication.setSalary(request.getSalary());
        loanApplication.setAmount(request.getAmount());
        loanApplication.setTenure(request.getTenure());
        loanApplication.setPurpose(request.getPurpose());
        return loanRepository.save(loanApplication);
    }

    public long count() {
        return loanRepository.count();
    }

    private LoanApplication findApplicationById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Application not found."));
    }

    private Status parseStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid application status.");
        }
    }
}
