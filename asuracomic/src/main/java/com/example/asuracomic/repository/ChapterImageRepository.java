package com.example.asuracomic.repository;

import com.example.asuracomic.entity.ChapterImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterImageRepository extends JpaRepository<ChapterImage, Long> {
    List<ChapterImage> findByChapterIdOrderByOrderIndexAsc(Long chapterId);
}