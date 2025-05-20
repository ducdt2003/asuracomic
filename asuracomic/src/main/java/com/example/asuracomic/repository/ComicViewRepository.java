package com.example.asuracomic.repository;

import com.example.asuracomic.entity.ComicView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ComicViewRepository extends JpaRepository<ComicView, Long> {
    // top comic view trong ngay
    @Query("""
        SELECT cv.comic, COUNT(cv.id) as views 
        FROM ComicView cv 
        WHERE cv.viewedAt >= :startOfDay AND cv.viewedAt <= :endOfDay
        GROUP BY cv.comic 
        ORDER BY views DESC
        """)
    List<Object[]> findTopViewedComicsToday(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);



    // Thêm: Lấy lượt xem trong tuần
    @Query("SELECT cv.comic, COUNT(cv) as viewCount FROM ComicView cv WHERE cv.viewedAt >= :start GROUP BY cv.comic ORDER BY viewCount DESC")
    List<Object[]> findTopViewedComicsWeekly(@Param("start") LocalDateTime start, Pageable pageable);

    // Thêm: Lấy lượt xem trong tháng
    @Query("SELECT cv.comic, COUNT(cv) as viewCount FROM ComicView cv WHERE cv.viewedAt >= :start GROUP BY cv.comic ORDER BY viewCount DESC")
    List<Object[]> findTopViewedComicsMonthly(@Param("start") LocalDateTime start, Pageable pageable);
}