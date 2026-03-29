package com.capgemini.adminservice.repository;

import com.capgemini.adminservice.entity.Decision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionRepository extends JpaRepository<Decision, Long> {
}