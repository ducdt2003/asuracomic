package com.example.asuracomic.service;

import com.example.asuracomic.dto.ComicCarouselDTO;

import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.repository.ComicGenreRepository;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.repository.ComicViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComicService {
    private final ComicRepository comicRepository;
    private final ComicViewRepository comicViewRepository;
    private final ComicGenreRepository comicGenreRepository;

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

    public List<Comic> getTopViewedComicsToday(int limit) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);

        List<Object[]> result = comicViewRepository.findTopViewedComicsToday(startOfDay, endOfDay, PageRequest.of(0, limit));

        return result.stream()
                .map(r -> (Comic) r[0])
                .toList();
    }



}

