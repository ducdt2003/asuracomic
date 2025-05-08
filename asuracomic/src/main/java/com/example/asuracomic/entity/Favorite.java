package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorites", indexes = @Index(name = "idx_user_id_comic_id", columnList = "user_id, comic_id", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của bản ghi, dùng để tham chiếu

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng thêm truyện vào yêu thích, liên kết để hiển thị danh sách yêu thích

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic; // Truyện được thêm vào yêu thích, hỗ trợ cá nhân hóa trải nghiệm

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm thêm vào yêu thích, dùng để sắp xếp hoặc thống kê
}
