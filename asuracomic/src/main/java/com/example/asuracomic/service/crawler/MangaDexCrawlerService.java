package com.example.asuracomic.service.crawler;

import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.entity.ComicGenre;
import com.example.asuracomic.entity.Genre;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import com.example.asuracomic.repository.ComicGenreRepository;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.repository.GenreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MangaDexCrawlerService {

    private static final String API_URL = "https://api.mangadex.org";
    private static final String COVER_URL = "https://uploads.mangadex.org/covers";
    private static final String USER_AGENT = "AsuraRank/1.0 (metadata indexer)";
    private static final int LIMIT = 50;
    private static final int TIMEOUT_MS = 30_000;

    private final ComicRepository comicRepository;
    private final GenreRepository genreRepository;
    private final ComicGenreRepository comicGenreRepository;
    private final ObjectMapper objectMapper;
    private final Slugify slugify = Slugify.builder().build();
    private Integer nextOffset;

    public record CrawlResult(int imported, int updated, int skipped, int offset,
                              int nextOffset, List<String> errors) {}

    @Transactional
    public synchronized CrawlResult crawlAndImport() {
        int imported = 0;
        int updated = 0;
        int skipped = 0;
        int offset = getNextOffset();
        List<String> errors = new ArrayList<>();

        try {
            JsonNode root = fetchMangaList(offset);
            JsonNode data = root.path("data");
            if (!data.isArray()) {
                throw new IllegalStateException("MangaDex response does not contain a data array");
            }

            List<String> mangaIds = new ArrayList<>();
            data.forEach(item -> mangaIds.add(item.path("id").asText()));
            Map<String, BigDecimal> ratings = fetchRatings(mangaIds);

            for (JsonNode manga : data) {
                String mangaId = manga.path("id").asText();
                try {
                    String result = importManga(manga, ratings.getOrDefault(mangaId, BigDecimal.ZERO));
                    switch (result) {
                        case "imported" -> imported++;
                        case "updated" -> updated++;
                        default -> skipped++;
                    }
                } catch (Exception exception) {
                    skipped++;
                    String message = "MangaDex id=" + mangaId + ": " + exception.getMessage();
                    errors.add(message);
                    log.warn("[MangaDex] {}", message);
                }
            }
            nextOffset = offset + data.size();
        } catch (Exception exception) {
            errors.add("Không thể lấy dữ liệu MangaDex: " + exception.getMessage());
            log.error("[MangaDex] Crawl failed", exception);
        }

        log.info("[MangaDex] Done: offset={}, nextOffset={}, imported={}, updated={}, skipped={}, errors={}",
                offset, getNextOffset(), imported, updated, skipped, errors.size());
        return new CrawlResult(imported, updated, skipped, offset, getNextOffset(), errors);
    }

    private int getNextOffset() {
        if (nextOffset == null) {
            long mangaDexComics = comicRepository.countBySerializationStartingWith("MangaDex:");
            nextOffset = Math.toIntExact(((mangaDexComics + LIMIT - 1) / LIMIT) * LIMIT);
        }
        return nextOffset;
    }

    private JsonNode fetchMangaList(int offset) throws Exception {
        Connection connection = Jsoup.connect(API_URL + "/manga")
                .ignoreContentType(true)
                .userAgent(USER_AGENT)
                .header("Accept", "application/json")
                .timeout(TIMEOUT_MS)
                .data("limit", String.valueOf(LIMIT))
                .data("offset", String.valueOf(offset))
                .data("includes[]", "cover_art")
                .data("order[followedCount]", "desc")
                .data("contentRating[]", "safe")
                .data("contentRating[]", "suggestive");
        return objectMapper.readTree(connection.execute().body());
    }

    private Map<String, BigDecimal> fetchRatings(List<String> mangaIds) {
        Map<String, BigDecimal> ratings = new HashMap<>();
        if (mangaIds.isEmpty()) return ratings;

        try {
            Connection connection = Jsoup.connect(API_URL + "/statistics/manga")
                    .ignoreContentType(true)
                    .userAgent(USER_AGENT)
                    .header("Accept", "application/json")
                    .timeout(TIMEOUT_MS);
            mangaIds.forEach(id -> connection.data("manga[]", id));
            JsonNode statistics = objectMapper.readTree(connection.execute().body()).path("statistics");

            mangaIds.forEach(id -> {
                JsonNode average = statistics.path(id).path("rating").path("average");
                if (average.isNumber()) {
                    ratings.put(id, BigDecimal.valueOf(average.asDouble() / 2.0)
                            .setScale(1, RoundingMode.HALF_UP));
                }
            });
        } catch (Exception exception) {
            log.warn("[MangaDex] Ratings unavailable: {}", exception.getMessage());
        }
        return ratings;
    }

    private String importManga(JsonNode manga, BigDecimal rating) {
        String mangaId = manga.path("id").asText();
        JsonNode attributes = manga.path("attributes");
        String title = localizedValue(attributes.path("title"));
        String coverFile = coverFileName(manga.path("relationships"));
        if (mangaId.isBlank() || title == null || title.isBlank() || coverFile == null) return "skipped";

        String coverUrl = COVER_URL + "/" + mangaId + "/" + coverFile + ".256.jpg";
        String description = localizedValue(attributes.path("description"));
        ComicStatus status = parseStatus(attributes.path("status").asText());
        ComicType type = parseType(attributes.path("originalLanguage").asText());

        Comic comic = comicRepository.findFirstByTitleIgnoreCase(title).orElse(null);
        boolean exists = comic != null;
        if (!exists) {
            String slug = uniqueSlug(title, mangaId);
            comic = Comic.builder()
                    .title(title)
                    .slug(slug)
                    .description(description)
                    .coverImage(coverUrl)
                    .viewCount(0L)
                    .followCount(0L)
                    .averageRating(rating)
                    .status(status)
                    .type(type)
                    .serialization("MangaDex: https://mangadex.org/title/" + mangaId)
                    .isPublished(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            comic = comicRepository.save(comic);
        } else {
            if (comic.getDescription() == null || comic.getDescription().isBlank()) comic.setDescription(description);
            if (comic.getCoverImage() == null || comic.getCoverImage().isBlank()) comic.setCoverImage(coverUrl);
            if (rating.compareTo(BigDecimal.ZERO) > 0) comic.setAverageRating(rating);
            comic.setStatus(status);
            comic.setType(type);
            comic.setUpdatedAt(LocalDateTime.now());
            comic = comicRepository.save(comic);
        }

        importGenres(comic, attributes.path("tags"));
        return exists ? "updated" : "imported";
    }

    private void importGenres(Comic comic, JsonNode tags) {
        if (!tags.isArray()) return;
        Set<Long> existingGenreIds = new HashSet<>();
        comicGenreRepository.findByComicId(comic.getId())
                .forEach(link -> existingGenreIds.add(link.getGenre().getId()));

        for (JsonNode tag : tags) {
            String group = tag.path("attributes").path("group").asText();
            if (!"genre".equals(group) && !"theme".equals(group)) continue;
            String name = localizedValue(tag.path("attributes").path("name"));
            if (name == null || name.isBlank()) continue;
            String genreSlug = slugify.slugify(name);

            Genre genre = genreRepository.findBySlug(genreSlug);
            if (genre == null) genre = genreRepository.findByName(name).orElse(null);
            if (genre == null) {
                genre = genreRepository.save(Genre.builder()
                        .name(name)
                        .slug(genreSlug)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
            if (existingGenreIds.add(genre.getId())) {
                comicGenreRepository.save(ComicGenre.builder().comic(comic).genre(genre).build());
            }
        }
    }

    private String localizedValue(JsonNode values) {
        if (!values.isObject() || values.isEmpty()) return null;
        for (String language : List.of("en", "vi", "ja-ro", "ko-ro", "ja", "ko", "zh")) {
            JsonNode value = values.get(language);
            if (value != null && value.isTextual() && !value.asText().isBlank()) return value.asText();
        }
        var fields = values.fields();
        while (fields.hasNext()) {
            JsonNode value = fields.next().getValue();
            if (value.isTextual() && !value.asText().isBlank()) return value.asText();
        }
        return null;
    }

    private String coverFileName(JsonNode relationships) {
        if (!relationships.isArray()) return null;
        for (JsonNode relationship : relationships) {
            if ("cover_art".equals(relationship.path("type").asText())) {
                String fileName = relationship.path("attributes").path("fileName").asText();
                if (!fileName.isBlank()) return fileName;
            }
        }
        return null;
    }

    private String uniqueSlug(String title, String mangaId) {
        String base = slugify.slugify(title);
        if (base.isBlank()) base = "mangadex";
        if (!comicRepository.existsBySlug(base)) return base;
        return base + "-md-" + mangaId.substring(0, Math.min(8, mangaId.length()));
    }

    private ComicStatus parseStatus(String status) {
        return switch (status) {
            case "completed", "cancelled" -> ComicStatus.COMPLETED;
            default -> ComicStatus.ONGOING;
        };
    }

    private ComicType parseType(String language) {
        return switch (language) {
            case "ko" -> ComicType.MANHWA;
            case "zh", "zh-hk" -> ComicType.MANHUA;
            default -> ComicType.MANGA;
        };
    }
}
