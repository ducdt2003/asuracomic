package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// Định nghĩa interface ChapterRepository, mở rộng JpaRepository để cung cấp các phương thức thao tác với thực thể Chapter
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    // Phương thức tùy chỉnh để tìm kiếm một chương (Chapter) dựa trên comicId và slug
    // comicId là ID của truyện mà chương thuộc về, slug là chuỗi thân thiện với URL của chương
    // Trả về đối tượng Chapter nếu tìm thấy, null nếu không tìm thấy
    // Được sử dụng để lấy chương dựa trên URL, ví dụ: /comics/1/chapter-100
    Chapter findByComicIdAndSlug(Long comicId, String slug);

    // Phương thức tùy chỉnh để tìm kiếm một chương (Chapter) dựa trên comicId và chapterNumber
    // comicId là ID của truyện, chapterNumber là số thứ tự của chương (VD: 1, 2, 100)
    // Trả về đối tượng Chapter nếu tìm thấy, null nếu không tìm thấy
    // Được sử dụng để lấy chương dựa trên số thứ tự, hữu ích khi điều hướng giữa các chương
    Chapter findByComicIdAndChapterNumber(Long comicId, Integer chapterNumber);


    List<Chapter> findByComicOrderByChapterNumberDesc(Comic comic);
    Optional<Chapter> findTopByComicOrderByChapterNumberAsc(Comic comic); // Chapter đầu tiên
    Optional<Chapter> findTopByComicOrderByChapterNumberDesc(Comic comic); // Chapter mới nhất


    @Query("SELECT c FROM Chapter c WHERE c.comic.slug = :comicSlug AND c.slug = :chapterSlug")
    Optional<Chapter> findByComicSlugAndChapterSlug(String comicSlug, String chapterSlug);
}