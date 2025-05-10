package com.example.asuracomic.service;

import com.example.asuracomic.dto.ComicCarouselDTO;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.repository.ComicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComicService {
    private final ComicRepository comicRepository;


    // xữ lý lấy 5 truyện hot đánh giá cao nhất
    public List<ComicCarouselDTO> getHotComicsForCarousel() {
        return comicRepository.findTop5ByIsPublishedTrueOrderByAverageRatingDesc()
                .stream()
                .map(comic -> ComicCarouselDTO.builder()
                        .id(comic.getId())
                        .title(comic.getTitle())
                        .type(comic.getType())
                        .genres(comic.getComicGenres().stream()
                                .map(genre -> genre.getGenre().getName()) // assuming Genre has getName()
                                .collect(Collectors.joining(", ")))
                        .averageRating(comic.getAverageRating())
                        .summary(comic.getDescription())
                        .status(comic.getStatus())
                        .coverImage(comic.getCoverImage())
                        .author(comic.getComicAuthors().stream()
                                .map(author -> author.getAuthor().getName()) // assuming Author has getName()
                                .findFirst()
                                .orElse("Unknown"))
                        .build())
                .collect(Collectors.toList());
    }


}

