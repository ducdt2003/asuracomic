package com.example.asuracomic.repository;

import com.example.asuracomic.entity.ComicAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComicAuthorRepository extends JpaRepository<ComicAuthor, Long> {
}