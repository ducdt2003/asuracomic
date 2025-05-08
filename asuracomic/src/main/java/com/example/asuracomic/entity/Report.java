package com.example.asuracomic.entity;

import com.example.asuracomic.model.enums.ReportContentType;
import com.example.asuracomic.model.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của báo cáo, dùng để tham chiếu

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng gửi báo cáo, liên kết để theo dõi và thông báo kết quả xử lý

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportContentType contentType; // Loại nội dung bị báo cáo (COMIC, CHAPTER, COMMENT), xác định đối tượng xử lý

    @Column(nullable = false)
    private Long contentId; // ID của nội dung bị báo cáo, liên kết đến truyện, chương, hoặc bình luận cụ thể

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason; // Lý do báo cáo, cung cấp chi tiết cho quản trị viên để xử lý

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status; // Trạng thái: PENDING, RESOLVED, REJECTED, hiển thị trong dashboard quản trị

    @ManyToOne
    @JoinColumn(name = "resolved_by")
    private User resolvedBy; // Quản trị viên xử lý báo cáo, liên kết để theo dõi trách nhiệm (chỉ dành cho ADMIN)

    @Column(columnDefinition = "TEXT")
    private String resolutionNote; // Ghi chú về cách xử lý, lưu kết quả để tham khảo hoặc thông báo cho người báo cáo

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm tạo báo cáo, dùng để quản lý và sắp xếp báo cáo

    @Column(nullable = false)
    private LocalDateTime updatedAt; // Thời điểm cập nhật báo cáo, theo dõi thay đổi trạng thái hoặc ghi chú
}