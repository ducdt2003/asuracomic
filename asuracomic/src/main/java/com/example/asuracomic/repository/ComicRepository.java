package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Comic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Định nghĩa interface ComicRepository, mở rộng JpaRepository để cung cấp các phương thức thao tác với thực thể Comic
@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {



    // 5 truyện có đánh giá cao nhất
    List<Comic> findTop5ByIsPublishedTrueOrderByAverageRatingDesc();


    /*@Query("SELECT c FROM Comic c WHERE c.isPublished = true")
    List<Comic> findAllPublished();

    @Query("SELECT c FROM Comic c WHERE c.isPublished = true AND c.updatedAt >= :startDate")
    List<Comic> findPublishedByDate(LocalDateTime startDate);*/

    // Top 10 truyện phổ biến trong 7 ngày qua
    // Top 10 weekly
    // Top 10 truyện phổ biến trong 7 ngày qua
    @Query(value = """
        WITH ViewCounts AS (
            SELECT 
                c.id, 
                c.title, 
                c.cover_image, 
                COALESCE(c.average_rating, 0) AS average_rating, 
                COUNT(cv.id) AS view_count,
                GROUP_CONCAT(DISTINCT g.name ORDER BY g.name) AS genres,
                c.slug
            FROM 
                comics c
                LEFT JOIN comic_views cv ON c.id = cv.comic_id 
                    AND cv.viewed_at >= :startDate
                LEFT JOIN comic_genres cg ON c.id = cg.comic_id
                LEFT JOIN genres g ON cg.genre_id = g.id
            WHERE 
                c.is_published = TRUE
            GROUP BY 
                c.id, c.title, c.cover_image, c.average_rating, c.slug
        ),
        MaxViews AS (
            SELECT COALESCE(MAX(view_count), 1) AS max_views
            FROM ViewCounts
        )
        SELECT 
            vc.id,
            vc.title,
            vc.cover_image,
            vc.average_rating,
            vc.view_count,
            vc.genres,
            CASE 
                WHEN vc.view_count = 0 THEN (0.3 * (vc.average_rating / 5.0) * 100)
                ELSE (0.7 * (vc.view_count / NULLIF(mv.max_views, 0)) * 100) + (0.3 * (vc.average_rating / 5.0) * 100)
            END AS combined_score,
            vc.slug
        FROM 
            ViewCounts vc
            CROSS JOIN MaxViews mv
        ORDER BY 
            combined_score DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findTop10Weekly(@Param("startDate") LocalDateTime startDate);

    // Top 10 truyện tháng
    @Query(value = """
        WITH ViewCounts AS (
            SELECT 
                c.id, 
                c.title, 
                c.cover_image, 
                COALESCE(c.average_rating, 0) AS average_rating, 
                COUNT(cv.id) AS view_count,
                GROUP_CONCAT(DISTINCT g.name ORDER BY g.name) AS genres,
                c.slug
            FROM 
                comics c
                LEFT JOIN comic_views cv ON c.id = cv.comic_id 
                    AND cv.viewed_at >= :startDate
                LEFT JOIN comic_genres cg ON c.id = cg.comic_id
                LEFT JOIN genres g ON cg.genre_id = g.id
            WHERE 
                c.is_published = TRUE
            GROUP BY 
                c.id, c.title, c.cover_image, c.average_rating, c.slug
        ),
        MaxViews AS (
            SELECT COALESCE(MAX(view_count), 1) AS max_views
            FROM ViewCounts
        )
        SELECT 
            vc.id,
            vc.title,
            vc.cover_image,
            vc.average_rating,
            vc.view_count,
            vc.genres,
            (0.7 * (vc.view_count / NULLIF(mv.max_views, 0)) * 100) + 
            (0.3 * (vc.average_rating / 5.0) * 100) AS combined_score,
            vc.slug
        FROM 
            ViewCounts vc
            CROSS JOIN MaxViews mv
        ORDER BY 
            combined_score DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findTop10Monthly(@Param("startDate") LocalDateTime startDate);




    /*@Query("SELECT c FROM Comic c WHERE c.isPublished = true ORDER BY c.updatedAt DESC")
    Page<Comic> findLatestPublishedComics(Pageable pageable);*/


    // Không cần @Query, chỉ cần dùng method name
    Page<Comic> findAllByOrderByUpdatedAtDesc(Pageable pageable);




    Optional<Comic> findBySlugAndIsPublishedTrue(String slug);


}
