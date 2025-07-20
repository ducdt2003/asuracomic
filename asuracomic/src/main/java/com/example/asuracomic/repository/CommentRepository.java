package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Comment;
import com.example.asuracomic.model.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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

}