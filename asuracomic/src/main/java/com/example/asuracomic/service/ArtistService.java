package com.example.asuracomic.service;


import com.example.asuracomic.entity.Artist;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.repository.ArtistRepository;
import com.example.asuracomic.repository.ComicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ComicRepository comicRepository;

    public Artist getArtistBySlug(String slug) {
        return artistRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public Page<Comic> getComicsByArtistSlug(String artistSlug, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return comicRepository.findByArtistSlug(artistSlug, pageable); // Sử dụng phương thức mới
    }
}