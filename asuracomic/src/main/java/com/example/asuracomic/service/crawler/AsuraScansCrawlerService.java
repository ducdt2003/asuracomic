package com.example.asuracomic.service.crawler;

import com.example.asuracomic.entity.*;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import com.example.asuracomic.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Crawler service để lấy danh sách truyện từ AsuraScans.com.
 * <p>
 * Asura Scans dùng Astro SSR, dữ liệu truyện được nhúng trực tiếp trong thuộc tính
 * {@code props} của các thẻ {@code <astro-island>}. Định dạng props là Astro's serialized
 * JSON: [0, scalar] | [1, array] | [0, {key: [0, val], ...}].
 * Không cần chạy JS - chỉ cần parse HTML tĩnh.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsuraScansCrawlerService {

    private final ComicRepository comicRepository;
    private final GenreRepository genreRepository;
    private final ComicGenreRepository comicGenreRepository;
    private final ObjectMapper objectMapper;

    private static final String BASE_URL = "https://asurascans.com/comics";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int TIMEOUT_MS = 30_000;

    /**
     * Kết quả trả về cho admin sau khi crawl.
     */
    public record CrawlResult(int imported, int updated, int skipped, List<String> errors) {}

    /**
     * Crawl trang chủ AsuraScans và import/update truyện vào DB.
     * Lấy cả danh sách Trending, Featured (Hero Carousel), và Popular.
     */
    @Transactional
    public CrawlResult crawlAndImport() {
        int imported = 0, updated = 0, skipped = 0;
        List<String> errors = new ArrayList<>();

        try {
            log.info("[Crawler] Fetching {}", BASE_URL);
            Document doc = Jsoup.connect(BASE_URL)
                    .userAgent(USER_AGENT)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .timeout(TIMEOUT_MS)
                    .get();

            // Lấy tất cả <astro-island> có chứa dữ liệu truyện
            Elements islands = doc.select("astro-island[props]");
            log.info("[Crawler] Found {} astro-island elements", islands.size());

            // Tập hợp các comic items để tránh trùng lặp trong 1 lần crawl
            Map<String, JsonNode> comicMap = new LinkedHashMap<>();

            for (Element island : islands) {
                String rawProps = island.attr("props");
                if (rawProps.isBlank()) continue;

                // Unescape HTML entities
                String propsJson = rawProps
                        .replace("&quot;", "\"")
                        .replace("&amp;", "&")
                        .replace("&#39;", "'")
                        .replace("&lt;", "<")
                        .replace("&gt;", ">");

                try {
                    JsonNode root = objectMapper.readTree(propsJson);

                    // Tìm node "items" hoặc "chapters" - chỉ xử lý node có "items" chứa truyện
                    JsonNode itemsNode = root.get("items");
                    if (itemsNode == null) continue;

                    // itemsNode là [1, [[0, {...}], [0, {...}], ...]]
                    // Decode Astro format
                    List<JsonNode> items = decodeAstroArray(itemsNode);
                    for (JsonNode raw : items) {
                        JsonNode item = decodeAstroObject(raw);
                        if (item == null) continue;

                        // Bỏ qua nếu không có slug (có thể là announcement items)
                        JsonNode slugNode = item.get("slug");
                        if (slugNode == null) continue;
                        String slug = decodeAstroScalar(slugNode);
                        if (slug == null || slug.isBlank()) continue;

                        // Kiểm tra nếu là comic item (có cover_url)
                        if (!item.has("cover_url") && !item.has("title")) continue;

                        comicMap.putIfAbsent(slug, item);
                    }
                } catch (Exception e) {
                    log.warn("[Crawler] Failed to parse island props: {}", e.getMessage());
                }
            }

            log.info("[Crawler] Total unique comics found: {}", comicMap.size());

            // Import từng comic
            for (Map.Entry<String, JsonNode> entry : comicMap.entrySet()) {
                try {
                    JsonNode item = entry.getValue();
                    String result = importComic(item);
                    switch (result) {
                        case "imported" -> imported++;
                        case "updated" -> updated++;
                        default -> skipped++;
                    }
                } catch (Exception e) {
                    String msg = "Lỗi import comic slug=" + entry.getKey() + ": " + e.getMessage();
                    log.error("[Crawler] {}", msg, e);
                    errors.add(msg);
                    skipped++;
                }
            }

        } catch (Exception e) {
            String msg = "Lỗi crawl trang " + BASE_URL + ": " + e.getMessage();
            log.error("[Crawler] {}", msg, e);
            errors.add(msg);
        }

        log.info("[Crawler] Done: imported={}, updated={}, skipped={}, errors={}",
                imported, updated, skipped, errors.size());
        return new CrawlResult(imported, updated, skipped, errors);
    }

    /**
     * Import hoặc update một comic item từ JSON node Astro-decoded.
     * @return "imported" | "updated" | "skipped"
     */
    private String importComic(JsonNode item) {
        String slug = decodeScalar(item, "slug");
        if (slug == null) return "skipped";

        String title = decodeScalar(item, "title");
        String coverUrl = decodeScalar(item, "cover_url");
        String description = decodeScalar(item, "description");
        String statusStr = decodeScalar(item, "status");
        String typeStr = decodeScalar(item, "type");
        String ratingStr = decodeScalar(item, "rating");
        String viewCountStr = decodeScalar(item, "view_count");

        // Bỏ qua các Astro item không phải truyện (ví dụ bài thông báo/tin tức).
        // Những item này có thể có title/slug nhưng không có ảnh bìa.
        if (title == null || title.isBlank() || coverUrl == null || coverUrl.isBlank()) {
            return "skipped";
        }

        // Strip HTML tags từ description
        if (description != null) {
            description = Jsoup.parse(description).text();
        }

        ComicStatus status = parseStatus(statusStr);
        ComicType type = parseType(typeStr);
        BigDecimal rating = parseRating(ratingStr);
        long viewCount = parseLong(viewCountStr);

        boolean exists = comicRepository.existsBySlug(slug);
        Comic comic;

        if (!exists) {
            comic = Comic.builder()
                    .title(title)
                    .slug(slug)
                    .description(description)
                    .coverImage(coverUrl)
                    .status(status)
                    .type(type)
                    .averageRating(rating)
                    .viewCount(viewCount)
                    .followCount(0L)
                    .isPublished(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            comic = comicRepository.save(comic);
            log.debug("[Crawler] Imported: {}", slug);
        } else {
            comic = comicRepository.findBySlug(slug).orElseThrow();
            // Update metadata
            comic.setTitle(title);
            if (coverUrl != null) comic.setCoverImage(coverUrl);
            if (description != null) comic.setDescription(description);
            comic.setStatus(status);
            comic.setType(type);
            comic.setAverageRating(rating);
            if (viewCount > comic.getViewCount()) comic.setViewCount(viewCount);
            comic.setUpdatedAt(LocalDateTime.now());
            comic = comicRepository.save(comic);
            log.debug("[Crawler] Updated: {}", slug);
        }

        // Xử lý genres
        JsonNode genresRaw = item.get("genres");
        if (genresRaw != null) {
            importGenres(comic, genresRaw, !exists);
        }

        return exists ? "updated" : "imported";
    }

    /**
     * Import hoặc link genres cho comic.
     */
    private void importGenres(Comic comic, JsonNode genresRaw, boolean isNew) {
        List<JsonNode> genreItems = decodeAstroArray(genresRaw);

        // Nếu là truyện mới thì chỉ thêm, không xóa (tránh orphan)
        // Nếu đã tồn tại thì skip genre đã có
        Set<Long> existingGenreIds = new HashSet<>();
        if (!isNew && comic.getComicGenres() != null) {
            comic.getComicGenres().forEach(cg -> existingGenreIds.add(cg.getGenre().getId()));
        }

        for (JsonNode rawGenre : genreItems) {
            JsonNode genre = decodeAstroObject(rawGenre);
            if (genre == null) continue;

            String genreName = decodeScalar(genre, "name");
            String genreSlug = decodeScalar(genre, "slug");
            if (genreName == null || genreSlug == null) continue;

            // Tìm hoặc tạo genre
            Genre genreEntity = genreRepository.findBySlug(genreSlug);
            if (genreEntity == null) {
                genreEntity = genreRepository.findByName(genreName).orElse(null);
            }
            if (genreEntity == null) {
                genreEntity = Genre.builder()
                        .name(genreName)
                        .slug(genreSlug)
                        .createdAt(LocalDateTime.now())
                        .build();
                genreEntity = genreRepository.save(genreEntity);
                log.debug("[Crawler] Created genre: {}", genreName);
            }

            // Link nếu chưa có
            if (!existingGenreIds.contains(genreEntity.getId())) {
                ComicGenre comicGenre = ComicGenre.builder()
                        .comic(comic)
                        .genre(genreEntity)
                        .build();
                comicGenreRepository.save(comicGenre);
                existingGenreIds.add(genreEntity.getId());
            }
        }
    }

    // ───────────────────────────── Astro serialized JSON helpers ─────────────────────────────

    /**
     * Decode Astro serialized array: [1, [[0, item], [0, item], ...]]
     * Returns list of raw inner values.
     */
    private List<JsonNode> decodeAstroArray(JsonNode node) {
        List<JsonNode> result = new ArrayList<>();
        if (node == null || !node.isArray() || node.size() < 2) return result;
        // [1, [...]]
        int typeCode = node.get(0).asInt(-1);
        if (typeCode != 1) return result;
        JsonNode arr = node.get(1);
        if (!arr.isArray()) return result;
        for (JsonNode el : arr) {
            result.add(el); // each el is [0, item]
        }
        return result;
    }

    /**
     * Decode Astro scalar: [0, value] -> value
     */
    private String decodeAstroScalar(JsonNode node) {
        if (node == null) return null;
        if (node.isArray() && node.size() == 2 && node.get(0).asInt(-1) == 0) {
            JsonNode val = node.get(1);
            if (val.isNull() || val.isMissingNode()) return null;
            return val.asText();
        }
        if (node.isTextual()) return node.asText();
        if (node.isNumber()) return node.asText();
        return null;
    }

    /**
     * Decode Astro object: [0, {key: [0, val], ...}] -> {key: [0, val], ...}
     */
    private JsonNode decodeAstroObject(JsonNode node) {
        if (node == null) return null;
        if (node.isArray() && node.size() == 2 && node.get(0).asInt(-1) == 0) {
            return node.get(1);
        }
        if (node.isObject()) return node;
        return null;
    }

    /**
     * Lấy scalar string từ object field (field là [0, value] format).
     */
    private String decodeScalar(JsonNode obj, String field) {
        if (obj == null || !obj.has(field)) return null;
        return decodeAstroScalar(obj.get(field));
    }

    // ───────────────────────────── Parse helpers ─────────────────────────────

    private ComicStatus parseStatus(String s) {
        if (s == null) return ComicStatus.ONGOING;
        return switch (s.toLowerCase()) {
            case "completed", "complete" -> ComicStatus.COMPLETED;
            default -> ComicStatus.ONGOING;
        };
    }

    private ComicType parseType(String s) {
        if (s == null) return ComicType.MANHWA;
        return switch (s.toLowerCase()) {
            case "manga" -> ComicType.MANGA;
            case "manhua" -> ComicType.MANHUA;
            default -> ComicType.MANHWA;
        };
    }

    private BigDecimal parseRating(String s) {
        if (s == null) return BigDecimal.ZERO;
        try {
            // Rating asura từ 0-10, normalize về 0-5
            double raw = Double.parseDouble(s);
            double normalized = raw / 2.0;
            return BigDecimal.valueOf(Math.round(normalized * 10) / 10.0);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private long parseLong(String s) {
        if (s == null) return 0L;
        try { return Long.parseLong(s); }
        catch (NumberFormatException e) { return 0L; }
    }
}
