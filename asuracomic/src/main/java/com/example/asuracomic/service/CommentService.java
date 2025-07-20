package com.example.asuracomic.service;

import com.example.asuracomic.dto.CommentDTO;
import com.example.asuracomic.dto.CommentResponseDTO;
import com.example.asuracomic.entity.Comment;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Report;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.exception.CustomException;
import com.example.asuracomic.model.enums.CommentStatus;
import com.example.asuracomic.model.enums.ReportContentType;
import com.example.asuracomic.model.enums.ReportStatus;
import com.example.asuracomic.repository.ChapterRepository;
import com.example.asuracomic.repository.CommentRepository;
import com.example.asuracomic.repository.ReportRepository;
import com.example.asuracomic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final ReportRepository reportRepository;

    public List<CommentResponseDTO> getCommentsByChapter(Long chapterId) {
        List<Comment> topLevelComments = commentRepository.findByChapterIdAndParentCommentIsNull(chapterId);
        return topLevelComments.stream()
                .map(comment -> mapToResponseDTO(comment, true))
                .collect(Collectors.toList());
    }

    public CommentResponseDTO createComment(Long userId, CommentDTO commentDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Người dùng không tồn tại"));
        Chapter chapter = chapterRepository.findById(commentDTO.getChapterId())
                .orElseThrow(() -> new CustomException("Chương không tồn tại"));

        if (!chapter.isPublished()) {
            throw new CustomException("Chương chưa được xuất bản");
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setChapter(chapter);
        if (commentDTO.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(commentDTO.getParentCommentId())
                    .orElseThrow(() -> new CustomException("Bình luận cha không tồn tại"));
            comment.setParentComment(parentComment);
        }
        comment.setContent(commentDTO.getContent());
        comment.setStatus(CommentStatus.ACTIVE);
        comment.setEdited(false);  // đổi setIsEdited thành setEdited
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return mapToResponseDTO(savedComment, false);
    }

    public CommentResponseDTO editComment(Long commentId, Long userId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("Bình luận không tồn tại"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException("Người dùng không có quyền chỉnh sửa bình luận này");
        }

        comment.setContent(newContent);
        comment.setEdited(true);  // đổi setIsEdited thành setEdited
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return mapToResponseDTO(updatedComment, false);
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("Bình luận không tồn tại"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException("Người dùng không có quyền xóa bình luận này");
        }

        comment.setStatus(CommentStatus.DELETED);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public Report reportComment(Long commentId, Long userId, String reason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("Bình luận không tồn tại"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Người dùng không tồn tại"));

        Report report = new Report();
        report.setUser(user);
        report.setContentType(ReportContentType.COMMENT);
        report.setContentId(commentId);
        report.setReason(reason);
        report.setStatus(ReportStatus.PENDING);
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    private CommentResponseDTO mapToResponseDTO(Comment comment, boolean includeReplies) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setAvatar(comment.getUser().getAvatar());
        dto.setContent(comment.getContent());
        dto.setEdited(comment.isEdited());  // đổi setIsEdited thành setEdited
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        if (includeReplies) {
            List<Comment> replies = commentRepository.findByParentComment(comment);
            dto.setReplies(replies.stream()
                    .map(reply -> mapToResponseDTO(reply, true))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
