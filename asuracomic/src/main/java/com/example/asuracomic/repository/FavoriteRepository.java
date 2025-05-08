package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // Phương thức tìm kiếm một bản ghi Favorite dựa trên userId và comicId
    // Trả về Optional<Favorite> để xử lý trường hợp không tìm thấy bản ghi
    Optional<Favorite> findByUser_IdAndComic_Id(Long userId, Long comicId);

    // Phương thức kiểm tra xem một bản ghi Favorite có tồn tại không dựa trên userId và comicId
    // Trả về true nếu bản ghi tồn tại, false nếu không
    // Được sử dụng để tránh tạo trùng lặp dữ liệu trong bảng favorites
    boolean existsByUser_IdAndComic_Id(Long userId, Long comicId);
}