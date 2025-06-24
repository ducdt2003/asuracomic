package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Report;
import com.example.asuracomic.model.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    long countByStatus(ReportStatus status); // Phương thức đếm số báo cáo theo trạng thái
}