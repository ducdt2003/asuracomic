package com.example.asuracomic.service;

import com.example.asuracomic.dto.ComicCarouselDTO;

import com.example.asuracomic.dto.ComicTopDTO;
import com.example.asuracomic.dto.RelatedComicDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.entity.Comment;
import com.example.asuracomic.model.enums.CommentStatus;
import com.example.asuracomic.repository.ChapterRepository;
import com.example.asuracomic.repository.ComicGenreRepository;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.repository.ComicViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComicService {
    private final ComicRepository comicRepository;
    private final ComicViewRepository comicViewRepository;
    private final ChapterRepository chapterRepository;

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


    // Tính điểm kết hợp: 70% viewCount + 30% averageRating
    /*private double calculateCombinedScore(Comic comic) {
        double maxViews = comicRepository.findAllPublished().stream()
                .mapToLong(Comic::getViewCount)
                .max().orElse(1000); // Giả định max view là 1000 nếu không có dữ liệu
        double maxRating = 5.0; // Điểm đánh giá tối đa là 5

        double normalizedViews = (comic.getViewCount() / maxViews) * 100; // Chuẩn hóa về thang 0-100
        double normalizedRating = (comic.getAverageRating().doubleValue() / maxRating) * 100; // Chuẩn hóa về thang 0-100

        // Trọng số: 70% views, 30% rating
        return (0.7 * normalizedViews) + (0.3 * normalizedRating);
    }

    public List<Comic> getTop10CombinedWeekly() {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        return comicRepository.findPublishedByDate(startDate).stream()
                .sorted(Comparator.comparingDouble(this::calculateCombinedScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Comic> getTop10CombinedMonthly() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        return comicRepository.findPublishedByDate(startDate).stream()
                .sorted(Comparator.comparingDouble(this::calculateCombinedScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Comic> getTop10CombinedAll() {
        return comicRepository.findAllPublished().stream()
                .sorted(Comparator.comparingDouble(this::calculateCombinedScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }*/

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



   /* public List<Comic> getLatestComics(int page, int size) {
        Page<Comic> comicPage = comicRepository.findLatestPublishedComics(PageRequest.of(page, size));
        return comicPage.getContent();
    }*/

   /* public List<Comic> getAllComics(int page, int size) {
        Page<Comic> comicPage = comicRepository.findAllByOrderByUpdatedAtDesc(PageRequest.of(page, size));
        return comicPage.getContent();
    }*/

    // lấy all truyện trên database
    public Page<Comic> getComicPage(int page, int size) {
        return comicRepository.findAllByOrderByUpdatedAtDesc(PageRequest.of(page, size));
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



    // truyện liên quna
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







}