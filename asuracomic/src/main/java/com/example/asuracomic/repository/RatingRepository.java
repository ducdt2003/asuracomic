package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    // Phương thức tìm kiếm một bản ghi Rating dựa trên userId và comicId
    // Trả về Optional<Rating> để xử lý trường hợp không tìm thấy bản ghi
    Optional<Rating> findByUser_IdAndComic_Id(Long userId, Long comicId);

    // Phương thức kiểm tra xem một bản ghi Rating có tồn tại không dựa trên userId và comicId
    // Trả về true nếu bản ghi tồn tại, false nếu không
    // Được sử dụng để tránh tạo trùng lặp dữ liệu trong bảng ratings
    boolean existsByUser_IdAndComic_Id(Long userId, Long comicId);
}