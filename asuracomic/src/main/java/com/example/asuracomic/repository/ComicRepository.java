package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Định nghĩa interface ComicRepository, mở rộng JpaRepository để cung cấp các phương thức thao tác với thực thể Comic
@Repository
public interface ComicRepository extends JpaRepository<Comic, Long> {
    // 5 truyện có đánh giá cao nhất
    List<Comic> findTop5ByIsPublishedTrueOrderByAverageRatingDesc();


}
