package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người nhận thông báo

    @Column(nullable = false, length = 255)
    private String title; // Tiêu đề thông báo

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // Nội dung thông báo

    @Column(nullable = false)
    private boolean isRead = false; // Trạng thái đã đọc

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm gửi

    @Column(length = 255)
    private String link; // Liên kết đến nội dung liên quan (VD: chương mới)
}
