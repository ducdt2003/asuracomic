package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

// Định nghĩa interface AuthorRepository, mở rộng JpaRepository để cung cấp các phương thức thao tác với thực thể Author
public interface AuthorRepository extends JpaRepository<Author, Long> {
    // Phương thức tùy chỉnh để tìm kiếm một tác giả (Author) dựa trên trường slug
    // slug là chuỗi thân thiện với URL, duy nhất cho mỗi tác giả (được định nghĩa trong thực thể Author)
    // Trả về đối tượng Author nếu tìm thấy, null nếu không tìm thấy
    Author findBySlug(String slug); // Tham số slug là chuỗi cần tìm kiếm
}