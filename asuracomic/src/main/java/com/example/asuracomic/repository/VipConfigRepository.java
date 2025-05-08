package com.example.asuracomic.repository;

import com.example.asuracomic.entity.VipConfig;
import org.springframework.data.jpa.repository.JpaRepository;

// Định nghĩa interface VipConfigRepository, mở rộng JpaRepository để cung cấp các phương thức thao tác với thực thể VipConfig
public interface VipConfigRepository extends JpaRepository<VipConfig, Long> {
    // Phương thức tùy chỉnh để tìm kiếm một gói VIP (VipConfig) dựa trên trường slug
    // slug là chuỗi thân thiện với URL, duy nhất cho mỗi gói VIP (được định nghĩa trong thực thể VipConfig)
    // Trả về đối tượng VipConfig nếu tìm thấy, null nếu không tìm thấy
    // Được sử dụng để lấy thông tin gói VIP dựa trên URL, ví dụ: /vip/monthly-vip
    VipConfig findBySlug(String slug); // Tham số slug là chuỗi cần tìm kiếm
}