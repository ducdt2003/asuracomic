package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", indexes = @Index(name = "idx_user_id_comic_id", columnList = "user_id, comic_id", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của đánh giá, dùng để tham chiếu

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng đánh giá, liên kết để kiểm tra quyền đánh giá

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic; // Truyện được đánh giá, liên kết để tính điểm trung bình

    @Column(nullable = false)
    private Integer score; // Điểm đánh giá (1-5), góp phần tính average_rating trong bảng comics

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm đánh giá, dùng để sắp xếp hoặc thống kê
}