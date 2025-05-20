package com.example.asuracomic.repository;

import com.example.asuracomic.entity.ComicView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ComicViewRepository extends JpaRepository<ComicView, Long> {
    @Query("""
        SELECT cv.comic, COUNT(cv.id) as views 
        FROM ComicView cv 
        WHERE cv.viewedAt >= :startOfDay AND cv.viewedAt <= :endOfDay
        GROUP BY cv.comic 
        ORDER BY views DESC
        """)
    List<Object[]> findTopViewedComicsToday(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
}