package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comic_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComicView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của lượt xem, dùng để tham chiếu

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic; // Truyện được xem, liên kết để tăng view_count trong bảng comics

    @Column(nullable = false)
    private LocalDateTime viewedAt; // Thời điểm xem, dùng để thống kê lượt xem theo thời gian
}
