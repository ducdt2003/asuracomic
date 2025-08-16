package com.example.asuracomic.repository;

import com.example.asuracomic.entity.ComicArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComicArtistRepository extends JpaRepository<ComicArtist, Long> {
    void deleteByComicId(Long comicId);
}