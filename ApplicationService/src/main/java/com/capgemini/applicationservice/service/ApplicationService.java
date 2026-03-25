package com.capgemini.applicationservice.service;

import com.capgemini.applicationservice.dto.ApplicationRequest;
import com.capgemini.applicationservice.entity.LoanApplication;
import com.capgemini.applicationservice.entity.Status;
import com.capgemini.applicationservice.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private LoanRepository repo;

    public LoanApplication create(ApplicationRequest request, String email) {
        LoanApplication app = new LoanApplication();
        app.setUserEmail(email);
        app.setFullName(request.getFullName());
        app.setPhone(request.getPhone());
        app.setCompany(request.getCompany());
        app.setSalary(request.getSalary());
        app.setAmount(request.getAmount());
        app.setTenure(request.getTenure());
        app.setStatus(Status.DRAFT);
        return repo.save(app);
    }

    public LoanApplication submit(Long id) {
        LoanApplication app = repo.findById(id)
                .orElseThrow();
        app.setStatus(Status.SUBMITTED);
        return repo.save(app);
    }

    public List<LoanApplication> getMyApps(String email) {
        return repo.findByUserEmail(email);
    }

    public Status getStatus(Long id) {
        return repo.findById(id)
                .orElseThrow()
                .getStatus();
    }
}