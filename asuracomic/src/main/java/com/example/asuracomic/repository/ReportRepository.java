package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}