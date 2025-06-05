package com.example.asuracomic.service;


import com.example.asuracomic.entity.Author;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.repository.AuthorRepository;
import com.example.asuracomic.repository.ComicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ComicRepository comicRepository;

    public Author getAuthorBySlug(String slug) {
        return authorRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public Page<Comic> getComicsByAuthorSlug(String authorSlug, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return comicRepository.findByAuthorSlug(authorSlug, pageable);
    }
}