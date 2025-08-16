package com.example.asuracomic.repository;

import com.example.asuracomic.entity.ComicGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComicGenreRepository extends JpaRepository<ComicGenre, Long> {
    List<ComicGenre> findByComicId(Long comicId);
    void deleteByComicId(Long comicId);
}