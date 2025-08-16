package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Report;
import com.example.asuracomic.model.enums.ReportContentType;
import com.example.asuracomic.model.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Long> {
    long countByStatus(ReportStatus status); // Phương thức đếm số báo cáo theo trạng thái

    @Modifying
    @Query("DELETE FROM Report r WHERE r.contentType = :contentType AND r.contentId = :contentId")
    void deleteByContentTypeAndContentId(ReportContentType contentType, Long contentId);
}