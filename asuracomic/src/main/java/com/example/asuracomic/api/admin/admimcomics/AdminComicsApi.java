package com.example.asuracomic.api.admin.admimcomics;

import com.cloudinary.api.ApiResponse;
import com.cloudinary.provisioning.Account;
import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.dto.admin.*;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.ChapterImage;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import com.example.asuracomic.repository.ArtistRepository;
import com.example.asuracomic.repository.AuthorRepository;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.repository.GenreRepository;
import com.example.asuracomic.service.DashboardService;
import com.example.asuracomic.service.admin.admincomic.AdminChapterService;
import com.example.asuracomic.service.admin.admincomic.AdminComicService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asura/admin/comic")
@RequiredArgsConstructor
public class AdminComicsApi {

    private final DashboardService dashboardService;
    private final ComicRepository comicRepository;
    private final AdminComicService adminComicService;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final ArtistRepository artistRepository;
    private final AdminChapterService adminChapterService;

    /* ================= Dashboard ================= */

    @GetMapping
    public ResponseEntity<DashboardStatsDTO> getDashboard() {
        return ResponseEntity.ok(
                dashboardService.getDashboardStats()
        );
    }

    /* ================= Comic List ================= */

    @GetMapping("/content/detail")
    public ResponseEntity<Page<Comic>> getComicList(
            @RequestParam(defaultValue = "0") int page
    ) {
        Page<Comic> comicPage = dashboardService.getComicPage(
                PageRequest.of(page, 5)
        );
        return ResponseEntity.ok(comicPage);
    }

    /* ================= Comic By Type ================= */

    @GetMapping("/content/detail/type/{type}")
    public ResponseEntity<Page<Comic>> getComicByType(
            @PathVariable ComicType type,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(
                comicRepository.findByType(
                        type,
                        PageRequest.of(page, 5)
                )
        );
    }

    /* ================= Comic Type Overview ================= */

    // GET /api/asura/admin/comic/content/detail/type
    @GetMapping("/content/detail/type")
    public ResponseEntity<Map<String, Object>> getComicTypeOverview(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(
                Map.of(
                        "topRatedComics",
                        adminComicService.getTopRatedComics(page).getContent(),

                        "topViewedComics",
                        adminComicService.getTopViewedComics(page).getContent(),

                        "mangaCount",
                        comicRepository.countByType(ComicType.MANGA),

                        "manhwaCount",
                        comicRepository.countByType(ComicType.MANHWA),

                        "manhuaCount",
                        comicRepository.countByType(ComicType.MANHUA)
                )
        );
    }

    /* ================= Create Comic (Meta Data) ================= */


    // Load data cho form create
    @GetMapping("/content/create")
    public ResponseEntity<Map<String, Object>> getCreateComicMeta() {
        return ResponseEntity.ok(
                Map.of(
                        "statuses", ComicStatus.values(),
                        "types", ComicType.values(),
                        "genres", genreRepository.findAll(),
                        "authors", authorRepository.findAll(),
                        "artists", artistRepository.findAll()
                )
        );
    }

    /* ================= Create Comic ================= */

    // POST /api/asura/admin/comic/content/create
    @PostMapping("/content/create")
    public ResponseEntity<Map<String, String>> createComic(
            @RequestBody ComicCreateRequest request
    ) {
        adminComicService.createComic(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Tạo truyện thành công"));
    }

    // Lấy chi tiết truyện + chapter (phân trang)
    @GetMapping("/content/{slug}")
    public ResponseEntity<Map<String, Object>> getComicDetail(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Comic comic = dashboardService.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));

        Page<Chapter> chapterPage = dashboardService.getChaptersByComic(comic, page, size);

        return ResponseEntity.ok(Map.of(
                "comic", comic,
                "chapters", chapterPage
        ));
    }

    // Xóa truyện
    @DeleteMapping("/content/delete/{comicId}") // Dùng DELETE method cho đúng chuẩn REST
    public ResponseEntity<Map<String, String>> deleteComic(@PathVariable Long comicId) {
        adminComicService.deleteComic(comicId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa truyện thành công!"));
    }


    // Cập nhật truyện (Lưu ý: Nếu có gửi file, ta dùng @ModelAttribute thay vì @RequestBody)
    @PutMapping("/content/edit/{slug}")
    public ResponseEntity<Map<String, String>> updateComic(
            @PathVariable String slug,
            @ModelAttribute ComicUpdateForm form,
            @RequestParam(value = "coverImageFile", required = false) MultipartFile file) {

        form.setCoverImageFile(file);
        adminComicService.updateComic(slug, form);

        return ResponseEntity.ok(Map.of("message", "Cập nhật thông tin truyện thành công!"));
    }

    /* ================= Chapter Management ================= */

    // Lấy thông tin 1 chương để edit
    @GetMapping("/content/chapter/edit/{id}")
    public ResponseEntity<Chapter> getChapterEdit(@PathVariable Long id) {
        Chapter chapter = adminChapterService.getChapterById(id);
        // Sắp xếp ảnh theo index trước khi trả về
        chapter.getChapterImages().sort(Comparator.comparing(ChapterImage::getOrderIndex));
        return ResponseEntity.ok(chapter);
    }

    // Tạo chương mới cho truyện
    @PostMapping("/content/chapter/create/{comicId}")
    public ResponseEntity<Map<String, String>> createChapter(
            @PathVariable Long comicId,
            @ModelAttribute ChapterUpdateForm chapterRequest) {

        adminChapterService.createChapter(comicId, chapterRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Tải lên chương mới thành công!"));
    }

    // Cập nhật metadata của chương
    @PutMapping("/content/chapter/edit/{id}")
    public ResponseEntity<Map<String, String>> updateChapter(
            @PathVariable Long id,
            @RequestBody ChapterEditFormDTO form) {

        adminChapterService.updateChapter(id, form);
        return ResponseEntity.ok(Map.of("message", "Cập nhật chương thành công!"));
    }

    // Xóa 1 ảnh trong chương và sắp xếp lại
    @DeleteMapping("/content/chapter/edit/delete-image/{imageId}")
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable Long imageId) {
        Long chapterId = adminChapterService.deleteImageAndReorder(imageId);
        return ResponseEntity.ok(Map.of(
                "message", "Đã xóa trang truyện thành công!",
                "chapterId", chapterId
        ));
    }

    // Thêm ảnh mới vào chương
    @PostMapping("/content/chapter/edit/add-images/{chapterId}")
    public ResponseEntity<Map<String, String>> addChapterImages(
            @PathVariable Long chapterId,
            @RequestParam("images") List<MultipartFile> images) {

        adminChapterService.addImagesToChapter(chapterId, images);
        return ResponseEntity.ok(Map.of("message", "Đã thêm ảnh thành công!"));
    }

    // Xóa chương
    @DeleteMapping("/content/chapter/delete/{chapterId}")
    public ResponseEntity<Map<String, String>> deleteChapter(@PathVariable Long chapterId) {
        adminChapterService.deleteChapter(chapterId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa chương thành công!"));
    }

    // xóa all chapter
    @DeleteMapping("/content/chapter/delete/all/{comicId}")
    public ResponseEntity<Map<String, Object>> deleteAllChaptersByComic(
            @PathVariable Long comicId) {

        int deleted = adminChapterService.deleteAllChaptersByComic(comicId);

        return ResponseEntity.ok(Map.of(
                "message", "Đã xóa toàn bộ chương của truyện",
                "deletedChapters", deleted
        ));
    }

}
