package com.example.asuracomic.repository;

import com.example.asuracomic.entity.UnlockedChapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Định nghĩa interface UnlockedChapterRepository, mở rộng JpaRepository để cung cấp các phương thức thao tác với thực thể UnlockedChapter
public interface UnlockedChapterRepository extends JpaRepository<UnlockedChapter, Long> {
    // Phương thức tùy chỉnh để tìm kiếm một bản ghi UnlockedChapter dựa trên userId và chapterId
    // userId là ID của người dùng đã mở khóa chương, chapterId là ID của chương được mở khóa
    // Trả về Optional<UnlockedChapter> để xử lý trường hợp không tìm thấy bản ghi (tránh NullPointerException)
    // Được sử dụng để kiểm tra xem một người dùng đã mở khóa một chương cụ thể nào chưa
    Optional<UnlockedChapter> findByUser_IdAndChapter_Id(Long userId, Long chapterId); // Tham số userId và chapterId là các giá trị cần tìm kiếm

    boolean existsByUserIdAndChapterId(Long userId, Long chapterId);
}