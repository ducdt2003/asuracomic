package com.example.asuracomic.api.admin.admininteract;


import com.example.asuracomic.entity.Comment;
import com.example.asuracomic.entity.Report;
import com.example.asuracomic.repository.CommentRepository;
import com.example.asuracomic.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/asura/admin/interact")
@RequiredArgsConstructor
public class AdminInteractApi {
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;

    //Danh sách REPORT
    @GetMapping("/reports")
    public ResponseEntity<?> getReports(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        if (page < 1) page = 1;

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by("status").ascending()
                        .and(Sort.by("createdAt").descending())
        );

        Page<Report> reportPage = reportRepository.findAll(pageable);

        return ResponseEntity.ok(Map.of(
                "data", reportPage.getContent(),
                "currentPage", page,
                "totalPages", reportPage.getTotalPages(),
                "totalItems", reportPage.getTotalElements()
        ));
    }

    //Danh sách COMMENTS
    @GetMapping("/comments")
    public ResponseEntity<?> getComments(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        if (page < 1) page = 1;

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Comment> commentsPage = commentRepository.findAll(pageable);

        return ResponseEntity.ok(Map.of(
                "data", commentsPage.getContent(),
                "currentPage", page,
                "totalPages", commentsPage.getTotalPages(),
                "totalItems", commentsPage.getTotalElements()
        ));
    }
}
