package com.example.asuracomic.repository;

import com.example.asuracomic.entity.ComicGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComicGenreRepository extends JpaRepository<ComicGenre, Long> {
}