package com.example.asuracomic.service;

import com.example.asuracomic.dto.ComicCarouselDTO;
import com.example.asuracomic.dto.ComicTopDTO;
import com.example.asuracomic.dto.RelatedComicDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.entity.Comment;
import com.example.asuracomic.entity.Genre;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import com.example.asuracomic.model.enums.CommentStatus;
import com.example.asuracomic.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComicService {
    private final ComicRepository comicRepository;
    private final ComicViewRepository comicViewRepository;
    private final ChapterRepository chapterRepository;
    private final GenreRepository genreRepository;

    public Comic getComicById(Long id) {
        return comicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy truyện với ID: " + id));
    }

    // xữ lý lấy 5 truyện hot đánh giá cao nhất
    public List<ComicCarouselDTO> getHotComicsForCarousel() {
        return comicRepository.findTop5ByIsPublishedTrueOrderByAverageRatingDesc()
                .stream()
                .map(comic -> ComicCarouselDTO.builder()
                        .id(comic.getId())
                        .title(comic.getTitle())
                        .type(comic.getType())
                        .genres(comic.getComicGenres().stream()
                                .map(genre -> genre.getGenre().getName())
                                .collect(Collectors.joining(", ")))
                        .averageRating(comic.getAverageRating())
                        .summary(comic.getDescription())
                        .status(comic.getStatus())
                        .coverImage(comic.getCoverImage())
                        .author(comic.getComicAuthors().stream()
                                .map(author -> author.getAuthor().getName())
                                .findFirst()
                                .orElse("Unknown"))
                        .slug(comic.getSlug())
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

    // Lấy top 10 truyện tuần
    public List<ComicTopDTO> getTop10CombinedWeekly() {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        List<Object[]> result = comicRepository.findTop10Weekly(startDate);
        System.out.println("Raw result: " + result);
        return result.stream()
                .map(row -> new ComicTopDTO(
                        ((Number) row[0]).longValue(), // id
                        (String) row[1],               // title
                        (String) row[2],               // coverImage
                        ((Number) row[3]).doubleValue(), // averageRating
                        ((Number) row[4]).longValue(), // viewCount
                        (String) row[5],               // genres
                        ((Number) row[6]).doubleValue(), // combinedScore
                        (String) row[7]                // slug
                ))
                .collect(Collectors.toList());
    }

    // Lấy top 10 truyện tháng
    public List<ComicTopDTO> getTop10CombinedMonthly() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        return comicRepository.findTop10Monthly(startDate)
                .stream()
                .map(row -> new ComicTopDTO(
                        ((Number) row[0]).longValue(), // id
                        (String) row[1],               // title
                        (String) row[2],               // coverImage
                        ((Number) row[3]).doubleValue(), // averageRating
                        ((Number) row[4]).longValue(), // viewCount
                        (String) row[5],               // genres
                        ((Number) row[6]).doubleValue(), // combinedScore
                        (String) row[7]                // slug
                ))
                .collect(Collectors.toList());
    }



    // chi tiết truyện
    public Comic getComicDetailsBySlug(String slug) {
        return comicRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Comic not found"));
    }

    // chapter
    public Comic getComicBySlug(String slug) {
        return comicRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Comic not found"));
    }

    public List<Chapter> getChaptersByComic(Comic comic) {
        return chapterRepository.findByComicOrderByChapterNumberDesc(comic);
    }

    public Chapter getFirstChapter(Comic comic) {
        return chapterRepository.findTopByComicOrderByChapterNumberAsc(comic)
                .orElseThrow(() -> new RuntimeException("No chapter found"));
    }

    public Chapter getLatestChapter(Comic comic) {
        return chapterRepository.findTopByComicOrderByChapterNumberDesc(comic)
                .orElseThrow(() -> new RuntimeException("No chapter found"));
    }

    // truyện liên quan
    public List<RelatedComicDTO> getRelatedComics(Long comicId, int limit) {
        List<Comic> comics = comicRepository.findRelatedComics(comicId, PageRequest.of(0, limit));
        return comics.stream().map(comic -> {
            RelatedComicDTO dto = new RelatedComicDTO();
            dto.setId(comic.getId());
            dto.setTitle(comic.getTitle());
            dto.setSlug(comic.getSlug());
            dto.setCoverImage(comic.getCoverImage());
            dto.setStatus(comic.getStatus());
            dto.setAverageRating(comic.getAverageRating());
            Integer latestChapterNumber = comic.getChapters().stream()
                    .filter(Chapter::isPublished)
                    .map(Chapter::getChapterNumber)
                    .max(Integer::compareTo)
                    .orElse(0);
            dto.setLatestChapterNumber(latestChapterNumber);
            return dto;
        }).collect(Collectors.toList());
    }

    // New method to resolve the error
    public Chapter getChapterBySlug(Comic comic, String chapterSlug) {
        if (comic == null || chapterSlug == null) {
            return null;
        }
        return comic.getChapters().stream()
                .filter(chapter -> chapterSlug.equals(chapter.getSlug()))
                .findFirst()
                .orElse(null);
    }

    // lấy all truyện trên database
    public Page<Comic> getComicPage(int page, int size) {
        Page<Comic> comicPage = comicRepository.findAllByOrderByUpdatedAtDesc(PageRequest.of(page, size));

        comicPage.getContent().forEach(comic -> {
            List<Chapter> filteredChapters = comic.getChapters().stream()
                    .filter(Chapter::isPublished)
                    .sorted(Comparator.comparing(Chapter::getChapterNumber).reversed())
                    .limit(3)
                    .collect(Collectors.toList());

            comic.setChapters(filteredChapters);
        });

        return comicPage;
    }
    // New method for filtered comics
    public Page<Comic> getComics(String genre, String status, String type, String orderBy, int page, int size) {
        Sort sort = mapOrderByToSort(orderBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Kiểm tra nếu không có bộ lọc (tất cả giá trị là mặc định)
        if ((genre == null || genre.isEmpty()) &&
                (status == null || status.equals("ALL")) &&
                (type == null || type.equals("ALL"))) {
            Page<Comic> comicPage = comicRepository.findAllByOrderByUpdatedAtDesc(pageable);

            comicPage.getContent().forEach(comic -> {
                List<Chapter> filteredChapters = comic.getChapters().stream()
                        .filter(Chapter::isPublished)
                        .sorted(Comparator.comparing(Chapter::getChapterNumber).reversed())
                        .limit(1) // Chỉ lấy chương mới nhất
                        .collect(Collectors.toList());
                comic.setChapters(filteredChapters);
            });

            return comicPage;
        }

        // Xử lý khi có bộ lọc
        ComicStatus comicStatus = status != null && !status.equals("ALL") ? ComicStatus.valueOf(status) : null;
        ComicType comicType = type != null && !type.equals("ALL") ? ComicType.valueOf(type) : null;
        String genreSlug = genre != null && !genre.isEmpty() ? genre : null;

        Page<Comic> comicPage = comicRepository.findComicsByFilters(genreSlug, comicStatus, comicType, pageable);

        comicPage.getContent().forEach(comic -> {
            List<Chapter> filteredChapters = comic.getChapters().stream()
                    .filter(Chapter::isPublished)
                    .sorted(Comparator.comparing(Chapter::getChapterNumber).reversed())
                    .limit(1) // Chỉ lấy chương mới nhất
                    .collect(Collectors.toList());
            comic.setChapters(filteredChapters);
        });

        return comicPage;
    }

    public List<Genre> getAllGenres() {
        return comicRepository.findGenresWithPublishedComics();
    }

    private Sort mapOrderByToSort(String orderBy) {
        switch (orderBy != null ? orderBy : "lastUpdated") {
            case "rating":
                return Sort.by(Sort.Order.desc("averageRating"));
            case "name":
                return Sort.by(Sort.Order.asc("title"));
            default:
                return Sort.by(Sort.Order.desc("updatedAt"));
        }
    }

    // tìm kiếm truyện theo title contaile
    public List<Comic> findByTitleContainingIgnoreCase(String title) {
        return comicRepository.findByTitleContainingIgnoreCase(title);
    }
}