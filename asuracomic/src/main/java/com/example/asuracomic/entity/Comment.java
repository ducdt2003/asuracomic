package com.example.asuracomic.entity;


import com.example.asuracomic.model.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của bình luận, dùng để tham chiếu

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng viết bình luận, hiển thị username và avatar trong phần bình luận

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter; // Chương liên quan, hiển thị bình luận dưới trang đọc chương

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment; // Bình luận cha (nếu là trả lời), hỗ trợ bình luận dạng cây

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // Nội dung bình luận, hiển thị trên giao diện, hỗ trợ tương tác cộng đồng

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status; // Trạng thái: ACTIVE (hiển thị), REPORTED (bị báo cáo), DELETED (đã xóa), hỗ trợ kiểm duyệt

    @Column(nullable = false)
    private boolean isEdited = false; // Cho biết bình luận đã được chỉnh sửa, hiển thị nhãn "đã chỉnh sửa"

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm tạo bình luận, dùng để sắp xếp bình luận theo thời gian

    @Column(nullable = false)
    private LocalDateTime updatedAt; // Thời điểm cập nhật bình luận, theo dõi chỉnh sửa hoặc thay đổi trạng thái
}
