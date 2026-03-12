package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Comment;
import com.example.asuracomic.model.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Lấy danh sách bình luận cấp cao nhất (top-level) của một chương.
     *
     * @param chapterId ID của chương
     * @return Danh sách Comment
     */
    List<Comment> findByChapterIdAndParentCommentIsNull(Long chapterId);

    /**
     * Lấy danh sách bình luận con của một bình luận cha.
     *
     * @param parentComment Bình luận cha
     * @return Danh sách Comment
     */
    List<Comment> findByParentComment(Comment parentComment);

    List<Comment> findByChapterAndStatusOrderByCreatedAtDesc(Chapter chapter, CommentStatus status);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.chapter.id = :chapterId")
    void deleteByChapterId(Long chapterId);

    // Lấy các bình luận gốc (không có parent) của một chương, sắp xếp mới nhất lên đầu
    Page<Comment> findByChapterIdAndParentCommentIsNull(Long chapterId, Pageable pageable);

    // Lấy danh sách reply cho một bình luận cha
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentId);


    @Modifying
    @Query("DELETE FROM Comment c WHERE c.chapter.id IN :chapterIds")
    void deleteByChapterIdIn(@Param("chapterIds") List<Long> chapterIds);
}