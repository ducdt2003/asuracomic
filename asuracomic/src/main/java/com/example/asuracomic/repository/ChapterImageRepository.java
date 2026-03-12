package com.example.asuracomic.repository;

import com.example.asuracomic.entity.ChapterImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChapterImageRepository extends JpaRepository<ChapterImage, Long> {
    List<ChapterImage> findByChapterIdOrderByOrderIndexAsc(Long chapterId);

    @Modifying
    @Query("DELETE FROM ChapterImage ci WHERE ci.chapter.id = :chapterId")
    void deleteByChapterId(@Param("chapterId") Long chapterId);


    @Modifying
    @Query("DELETE FROM ChapterImage ci WHERE ci.chapter.id IN :chapterIds")
    void deleteByChapterIdIn(@Param("chapterIds") List<Long> chapterIds);
}