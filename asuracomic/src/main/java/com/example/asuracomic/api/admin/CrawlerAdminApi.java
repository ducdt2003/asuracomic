package com.example.asuracomic.api.admin;

import com.example.asuracomic.service.crawler.AsuraScansCrawlerService;
import com.example.asuracomic.service.crawler.AsuraScansCrawlerService.CrawlResult;
import com.example.asuracomic.service.crawler.MangaDexCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API cho admin để kích hoạt crawler lấy truyện từ AsuraScans.
 * <p>
 * Endpoint: POST /api/asura/admin/crawl/comics
 * Response JSON: { imported, updated, skipped, errors, message }
 */
@RestController
@RequestMapping("/api/asura/admin/crawl")
@RequiredArgsConstructor
public class CrawlerAdminApi {

    private final AsuraScansCrawlerService crawlerService;
    private final MangaDexCrawlerService mangaDexCrawlerService;

    /**
     * Kích hoạt crawl AsuraScans và import truyện vào DB.
     * Gọi từ Admin Panel bằng AJAX / fetch.
     */
    @PostMapping("/comics")
    public ResponseEntity<Map<String, Object>> crawlComics() {
        CrawlResult result = crawlerService.crawlAndImport();

        Map<String, Object> response = new HashMap<>();
        response.put("imported", result.imported());
        response.put("updated", result.updated());
        response.put("skipped", result.skipped());
        response.put("errors", result.errors());
        response.put("message", String.format(
                "Crawl xong! %d truyện mới | %d cập nhật | %d bỏ qua",
                result.imported(), result.updated(), result.skipped()
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy metadata truyện phổ biến từ API MangaDex; không tải chapter.
     */
    @PostMapping("/mangadex")
    public ResponseEntity<Map<String, Object>> crawlMangaDex() {
        MangaDexCrawlerService.CrawlResult result = mangaDexCrawlerService.crawlAndImport();

        Map<String, Object> response = new HashMap<>();
        response.put("imported", result.imported());
        response.put("updated", result.updated());
        response.put("skipped", result.skipped());
        response.put("offset", result.offset());
        response.put("nextOffset", result.nextOffset());
        response.put("errors", result.errors());
        response.put("message", String.format(
                "MangaDex mục %d-%d xong! %d truyện mới | %d cập nhật | %d bỏ qua",
                result.offset() + 1, result.nextOffset(),
                result.imported(), result.updated(), result.skipped()
        ));
        return ResponseEntity.ok(response);
    }
}
