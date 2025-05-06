package com.example.asuracomic.controller.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chapter_images", indexes = @Index(name = "idx_chapter_id_order_index", columnList = "chapter_id, order_index", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của ảnh, dùng để tham chiếu trong cơ sở dữ liệu

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter; // Chương mà ảnh thuộc về, liên kết để hiển thị ảnh trong trang đọc

    @Column(nullable = false, length = 255)
    private String imageUrl; // Đường dẫn đến ảnh, hiển thị nội dung chính của chương khi người dùng đọc

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex; // Thứ tự ảnh trong chương (VD: 1, 2, 3), đảm bảo hiển thị đúng trình tự

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm thêm ảnh, dùng để quản lý hoặc theo dõi cập nhật nội dung
}
