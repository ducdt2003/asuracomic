package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

// Định nghĩa interface ArtistRepository, mở rộng JpaRepository để cung cấp các phương thức thao tác với thực thể Artist
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    // Phương thức tùy chỉnh để tìm kiếm một họa sĩ (Artist) dựa trên trường slug
    // slug là chuỗi thân thiện với URL, duy nhất cho mỗi họa sĩ (được định nghĩa trong thực thể Artist)
    // Trả về đối tượng Artist nếu tìm thấy, null nếu không tìm thấy
    Artist findBySlug(String slug); // Tham số slug là chuỗi cần tìm kiếm
}