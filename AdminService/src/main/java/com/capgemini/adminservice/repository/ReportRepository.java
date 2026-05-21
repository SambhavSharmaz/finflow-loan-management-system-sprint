package com.capgemini.adminservice.repository;

import com.capgemini.adminservice.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByOrderByGeneratedAtDesc();
}
