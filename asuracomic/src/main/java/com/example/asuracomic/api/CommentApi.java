package com.example.asuracomic.api;


import com.example.asuracomic.dto.CommentDTO;
import com.example.asuracomic.dto.CommentResponseDTO;
import com.example.asuracomic.dto.ReportDTO;
import com.example.asuracomic.entity.Report;
import com.example.asuracomic.exception.CustomException;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.service.CommentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller xử lý các API liên quan đến bình luận.
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentApi {

    private final CommentService commentService;
    private final UserRepository userRepository;
    private final HttpSession session; // Inject HttpSession để lấy currentUser

    /**
     * Lấy danh sách bình luận của một chương.
     *
     * @param chapterId ID của chương
     * @return Danh sách CommentResponseDTO
     */
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByChapter(@PathVariable Long chapterId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByChapter(chapterId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Thêm bình luận mới (bình luận cha hoặc con).
     *
     * @param commentDTO DTO chứa thông tin bình luận
     * @return CommentResponseDTO của bình luận vừa tạo
     */
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@Valid @RequestBody CommentDTO commentDTO) {
        Long userId = getCurrentUserId(); // Lấy userId từ session
        CommentResponseDTO comment = commentService.createComment(userId, commentDTO);
        return ResponseEntity.ok(comment);
    }

    /**
     * Chỉnh sửa bình luận.
     *
     * @param commentId ID của bình luận
     * @param commentDTO DTO chứa nội dung mới
     * @return CommentResponseDTO của bình luận đã chỉnh sửa
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> editComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDTO commentDTO) {
        Long userId = getCurrentUserId(); // Lấy userId từ session
        CommentResponseDTO updatedComment = commentService.editComment(commentId, userId, commentDTO.getContent());
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * Xóa bình luận (xóa mềm).
     *
     * @param commentId ID của bình luận
     * @return HTTP 204 No Content nếu xóa thành công
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = getCurrentUserId(); // Lấy userId từ session
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Báo cáo bình luận vi phạm.
     *
     * @param commentId ID của bình luận
     * @param reportDTO DTO chứa lý do báo cáo
     * @return Bản ghi Report
     */
    @PostMapping("/{commentId}/report")
    public ResponseEntity<Report> reportComment(
            @PathVariable Long commentId,
            @RequestBody ReportDTO reportDTO) {
        Long userId = getCurrentUserId(); // Lấy userId từ session
        Report report = commentService.reportComment(commentId, userId, reportDTO.getReason());
        return ResponseEntity.ok(report);
    }

    /**
     * Lấy ID người dùng hiện tại từ HttpSession.
     *
     * @return ID của người dùng
     * @throws CustomException nếu không tìm thấy người dùng trong session
     */
    private Long getCurrentUserId() {
        Object currentUser = session.getAttribute("currentUser"); // Lấy thông tin currentUser từ session
        if (currentUser == null) {
            throw new CustomException("Người dùng chưa đăng nhập"); // Ném lỗi nếu chưa đăng nhập
        }
        return ((com.example.asuracomic.dto.UserDTO) currentUser).getId(); // Ép kiểu và lấy id
    }
}