package com.example.asuracomic.service;

import com.example.asuracomic.dto.ComicCarouselDTO;

import com.example.asuracomic.dto.ComicTopDTO;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.repository.ComicGenreRepository;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.repository.ComicViewRepository;
import lombok.RequiredArgsConstructor;
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
    private final ComicGenreRepository comicGenreRepository;

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
    /*public List<ComicTopDTO> getTop10CombinedWeekly() {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        return comicRepository.findTop10Weekly(startDate)
                .stream()
                .map(row -> new ComicTopDTO(
                        ((Number) row[0]).longValue(), // id
                        (String) row[1],               // title
                        (String) row[2],               // cover_image
                        ((Number) row[3]).doubleValue(), // average_rating
                        ((Number) row[4]).longValue(), // view_count
                        (String) row[5],               // genres
                        ((Number) row[6]).doubleValue() // combined_score
                ))
                .collect(Collectors.toList());
    }*/
    public List<ComicTopDTO> getTop10CombinedWeekly() {
        LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);
        List<Object[]> result = comicRepository.findTop10Weekly(startDate);
        System.out.println("Raw result: " + result);
        return result.stream()
                .map(row -> new ComicTopDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).doubleValue(),
                        ((Number) row[4]).longValue(),
                        (String) row[5],
                        ((Number) row[6]).doubleValue()
                ))
                .collect(Collectors.toList());
    }

    // Lấy top 10 truyện tháng
    public List<ComicTopDTO> getTop10CombinedMonthly() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        return comicRepository.findTop10Monthly(startDate)
                .stream()
                .map(row -> new ComicTopDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).doubleValue(),
                        ((Number) row[4]).longValue(),
                        (String) row[5],
                        ((Number) row[6]).doubleValue()
                ))
                .collect(Collectors.toList());
    }

    // Lấy top 10 truyện tất cả thời gian
    public List<ComicTopDTO> getTop10CombinedAll() {
        return comicRepository.findTop10All()
                .stream()
                .map(row -> new ComicTopDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).doubleValue(),
                        ((Number) row[4]).longValue(),
                        (String) row[5],
                        ((Number) row[6]).doubleValue()
                ))
                .collect(Collectors.toList());
    }


}