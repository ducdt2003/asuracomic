package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

// Định nghĩa interface GenreRepository, mở rộng JpaRepository để cung cấp các phương thức thao tác với thực thể Genre
public interface GenreRepository extends JpaRepository<Genre, Long> {
    // Phương thức tùy chỉnh để tìm kiếm một thể loại (Genre) dựa trên trường slug
    // slug là chuỗi thân thiện với URL, duy nhất cho mỗi thể loại (được định nghĩa trong thực thể Genre)
    // Trả về đối tượng Genre nếu tìm thấy, null nếu không tìm thấy
    // Được sử dụng để lấy thông tin thể loại dựa trên URL, ví dụ: /genres/action
    Genre findBySlug(String slug); // Tham số slug là chuỗi cần tìm kiếm
}