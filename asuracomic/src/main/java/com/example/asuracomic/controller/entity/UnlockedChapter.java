package com.example.asuracomic.controller.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "unlocked_chapters", indexes = @Index(name = "idx_user_id_chapter_id", columnList = "user_id, chapter_id", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnlockedChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của bản ghi, dùng để tham chiếu

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng đã mở khóa, liên kết để kiểm tra quyền truy cập

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter; // Chương đã mở khóa, hiển thị trong danh sách chương có thể đọc

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal coinSpent = BigDecimal.ZERO; // Số coin dùng để mở khóa, lưu để theo dõi chi tiêu

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction; // Giao dịch liên quan, liên kết để kiểm tra hoặc đối chiếu

    @Column(nullable = false)
    private LocalDateTime unlockedAt; // Thời điểm mở khóa, dùng để theo dõi lịch sử, hiển thị trong hồ sơ
}
