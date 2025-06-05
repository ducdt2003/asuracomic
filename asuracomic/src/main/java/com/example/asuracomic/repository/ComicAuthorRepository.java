package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.entity.ComicAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComicAuthorRepository extends JpaRepository<ComicAuthor, Long> {
    @Query("SELECT ca.comic FROM ComicAuthor ca WHERE ca.author.slug = :authorSlug AND ca.comic.isPublished = true")
    Page<Comic> findComicsByAuthorSlug(String authorSlug, Pageable pageable);
}