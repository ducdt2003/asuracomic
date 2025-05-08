package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Comic;
import org.springframework.data.jpa.repository.JpaRepository;

// Định nghĩa interface ComicRepository, mở rộng JpaRepository để cung cấp các phương thức thao tác với thực thể Comic
public interface ComicRepository extends JpaRepository<Comic, Long> {
    // Phương thức tùy chỉnh để tìm kiếm một truyện (Comic) dựa trên trường slug
    // slug là chuỗi thân thiện với URL, duy nhất cho mỗi truyện (được định nghĩa trong thực thể Comic)
    // Trả về đối tượng Comic nếu tìm thấy, null nếu không tìm thấy
    // Được sử dụng để lấy thông tin truyện dựa trên URL, ví dụ: /comics/one-piece
    Comic findBySlug(String slug); // Tham số slug là chuỗi cần tìm kiếm
}