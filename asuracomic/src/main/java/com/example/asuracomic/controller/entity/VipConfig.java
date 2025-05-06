package com.example.asuracomic.controller.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vip_config", indexes = @Index(name = "idx_slug", columnList = "slug", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VipConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của gói VIP, dùng để tham dưỡng trong bảng transactions

    @Column(nullable = false, length = 100)
    private String name; // Tên gói VIP (VD: Monthly VIP), hiển thị trên giao diện khi mua

    @Column(unique = true, nullable = false, length = 100)
    private String slug; // Chuỗi thân thiện với URL (VD: monthly-vip), dùng để tạo link trang mua VIP

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal coinPrice; // Giá coin để mua gói VIP, hiển thị tùy chọn thanh toán

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal moneyPrice; // Giá tiền thật để mua gói VIP, hiển thị tùy chọn thanh toán

    @Column(nullable = false)
    private Integer durationDays; // Số ngày hiệu lực (VD: 30, 90), dùng để tính vip_expiry_date trong bảng users

    @Column(nullable = false)
    private boolean isActive = true; // Trạng thái gói: true (đang cung cấp), false (ngừng), kiểm soát hiển thị trên giao diện

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm tạo gói, dùng để quản lý hoặc theo dõi

    @Column(nullable = false)
    private LocalDateTime updatedAt; // Thời điểm cập nhật gói, theo dõi thay đổi (VD: chỉnh sửa giá, thời hạn)
}
