package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.entity.Genre;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {


    // 5 truyện có đánh giá cao nhất
    List<Comic> findTop5ByIsPublishedTrueOrderByAverageRatingDesc();

    // Top 10 truyện phổ biến trong 7 ngày qua
    /*@Query(value = """
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
    List<Object[]> findTop10Weekly(@Param("startDate") LocalDateTime startDate);*/
    @Query(value = """
    WITH ViewCounts AS (
        SELECT 
            c.id, 
            c.title, 
            c.cover_image, 
            COALESCE(c.average_rating, 0.0) AS average_rating, 
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
        COALESCE(
            CASE 
                WHEN vc.view_count = 0 THEN (0.3 * (vc.average_rating / 5.0) * 100)
                ELSE (0.7 * (vc.view_count / COALESCE(NULLIF(mv.max_views, 0), 1)) * 100) + (0.3 * (vc.average_rating / 5.0) * 100)
            END, 
            0.0
        ) AS combined_score,
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
    /*@Query(value = """
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
    List<Object[]> findTop10Monthly(@Param("startDate") LocalDateTime startDate);*/
    @Query(value = """
    WITH ViewCounts AS (
        SELECT 
            c.id, 
            c.title, 
            c.cover_image, 
            COALESCE(c.average_rating, 0.0) AS average_rating, 
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
        COALESCE(
            (0.7 * (vc.view_count / COALESCE(NULLIF(mv.max_views, 0), 1)) * 100) + 
            (0.3 * (vc.average_rating / 5.0) * 100), 
            0.0
        ) AS combined_score,
        vc.slug
    FROM 
        ViewCounts vc
        CROSS JOIN MaxViews mv
    ORDER BY 
        combined_score DESC
    LIMIT 10
""", nativeQuery = true)
    List<Object[]> findTop10Monthly(@Param("startDate") LocalDateTime startDate);

    // Không cần @Query, chỉ cần dùng method name
    Page<Comic> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    // chi tieets truyện và chapter
    Optional<Comic> findBySlug(String slug);

    // truyện liên quan
    @Query("SELECT DISTINCT c FROM Comic c " +
            "JOIN c.comicGenres cg " +
            "WHERE cg.genre.id IN (SELECT cg2.genre.id FROM ComicGenre cg2 WHERE cg2.comic.id = :comicId) " +
            "AND c.id != :comicId " +
            "AND c.isPublished = true " +
            "ORDER BY c.viewCount DESC, c.averageRating DESC")
    List<Comic> findRelatedComics(Long comicId, Pageable pageable);

    // truyện của tác giả
    @Query("SELECT c FROM Comic c JOIN c.comicAuthors ca WHERE ca.author.slug = :authorSlug")
    Page<Comic> findByAuthorSlug(String authorSlug, Pageable pageable);

    // truyện của họa sĩ
    @Query("SELECT c FROM Comic c JOIN c.comicArtists ca WHERE ca.artist.slug = :artistSlug")
    Page<Comic> findByArtistSlug(@Param("artistSlug") String artistSlug, Pageable pageable);

    // New method for filtering comics
    @Query("SELECT DISTINCT c FROM Comic c " +
            "LEFT JOIN c.comicGenres cg " +
            "WHERE (:genre IS NULL OR cg.genre.slug = :genre) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "AND (:type IS NULL OR c.type = :type) " +
            "AND c.isPublished = true " +
            "GROUP BY c.id")
    Page<Comic> findComicsByFilters(@Param("genre") String genre,
                                    @Param("status") ComicStatus status,
                                    @Param("type") ComicType type,
                                    Pageable pageable);

    // Fetch distinct genres with published comics
    @Query("SELECT DISTINCT g FROM Genre g JOIN g.comicGenres cg WHERE cg.comic.isPublished = true")
    List<Genre> findGenresWithPublishedComics();


    // tìm kiếm truyện
    List<Comic> findByTitleContainingIgnoreCase(String title);



}