package com.capgemini.applicationservice.repository;

import com.capgemini.applicationservice.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByUserEmail(String email);
}