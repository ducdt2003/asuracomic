package com.example.asuracomic.controller.web;

import com.example.asuracomic.dto.*;
import com.example.asuracomic.entity.*;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.repository.GenreRepository;
import com.example.asuracomic.service.ArtistService;
import com.example.asuracomic.service.AuthorService;
import com.example.asuracomic.service.ComicService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/asura")
@RequiredArgsConstructor
public class WebController {
    private final ComicService comicService;
    private final GenreRepository genreRepository;
    private final AuthorService authorService;
    private final ArtistService artistService;
    private final HttpSession session;

    // trang chủ
    @GetMapping
    public String homeLogin(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "30") int size,
                            Model model) {
        // top comics rating
        List<ComicCarouselDTO> hotComics = comicService.getHotComicsForCarousel();
        model.addAttribute("hotComics", hotComics);
        // top comics views today
        List<Comic> popularToday = comicService.getTopViewedComicsToday(5);
        model.addAttribute("popularToday", popularToday);
        // bảng xếp hạng
        // Lấy danh sách top 10 cho tuần, tháng, và tất cả thời gian
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();

        // Thêm vào model để hiển thị trên view
        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);

        Page<Comic> comicPage = comicService.getComicPage(page, size);
        model.addAttribute("comics", comicPage.getContent());
        model.addAttribute("totalPages", comicPage.getTotalPages());
        model.addAttribute("currentPage", page);

        return "web/web-main/home";
    }

    // trang chi tiết
    @GetMapping("/comic/{slug}")
    public String detail(@PathVariable String slug,
                         @RequestParam(required = false) String redirectToAuthor,
                         Model model) {
        // Lấy chi tiết truyện theo slug
        Comic comicDetail = comicService.getComicDetailsBySlug(slug);
        if (comicDetail == null) {
            return "redirect:/error"; // Hoặc chuyển hướng đến trang lỗi
        }

        // Check if redirect to author is requested
        if (redirectToAuthor != null && !redirectToAuthor.isEmpty()) {
            // Assuming the comic has a primary author, get the first author's slug
            List<Author> authors = comicDetail.getComicAuthors().stream()
                    .map(comicAuthor -> comicAuthor.getAuthor())
                    .collect(Collectors.toList());
            if (!authors.isEmpty()) {
                String authorSlug = authors.get(0).getSlug(); // Get the first author's slug
                return "redirect:/asura/authors/" + authorSlug;
            }
        }

        // Existing logic for the detail page
        model.addAttribute("comic", comicDetail);

        // Lấy danh sách chương
        List<Chapter> chapters = comicService.getChaptersByComic(comicDetail);
        Chapter firstChapter = comicService.getFirstChapter(comicDetail);
        Chapter latestChapter = comicService.getLatestChapter(comicDetail);

        model.addAttribute("chapters", chapters);
        model.addAttribute("firstChapter", firstChapter);
        model.addAttribute("latestChapter", latestChapter);

        // Lấy danh sách truyện liên quan
        List<RelatedComicDTO> relatedComics = comicService.getRelatedComics(comicDetail.getId(), 5);
        model.addAttribute("relatedComics", relatedComics);

        // Lấy danh sách top 10 cho tuần và tháng
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();

        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);

        return "web/web-main/detail";
    }

    // trang chapter
    @GetMapping("/comic/{comicSlug}/chapter/{chapterSlug}")
    public String chapter(@PathVariable String comicSlug, @PathVariable String chapterSlug, Model model) {
        // Lấy chi tiết truyện qua comicSlug
        Comic comic = comicService.getComicDetailsBySlug(comicSlug);
        if (comic == null) {
            return "redirect:/error";
        }

        // Lấy chương qua chapterSlug
        Chapter chapter = comicService.getChapterBySlug(comic, chapterSlug);
        if (chapter == null) {
            return "redirect:/error";
        }

        // Lấy danh sách truyện liên quan
        List<RelatedComicDTO> relatedComics = comicService.getRelatedComics(comic.getId(), 5);

        // Sắp xếp danh sách chương theo chapterNumber (tăng dần)
        List<Chapter> sortedChapters = comic.getChapters().stream()
                .filter(ch -> ch.isPublished()) // Chỉ lấy các chương đã xuất bản
                .sorted(Comparator.comparingInt(Chapter::getChapterNumber))
                .collect(Collectors.toList());

        // Tìm chương trước và chương sau
        Chapter previousChapter = null;
        Chapter nextChapter = null;
        for (int i = 0; i < sortedChapters.size(); i++) {
            Chapter current = sortedChapters.get(i);
            if (current.getId().equals(chapter.getId())) {
                if (i > 0) {
                    previousChapter = sortedChapters.get(i - 1); // Chương trước
                }
                if (i < sortedChapters.size() - 1) {
                    nextChapter = sortedChapters.get(i + 1); // Chương sau
                }
                break;
            }
        }

        // Thêm dữ liệu vào model
        model.addAttribute("comic", comic);
        model.addAttribute("chapter", chapter);
        model.addAttribute("relatedComics", relatedComics);
        model.addAttribute("sortedChapters", sortedChapters);
        model.addAttribute("previousChapter", previousChapter);
        model.addAttribute("nextChapter", nextChapter);
        model.addAttribute("chapter", chapter);

        return "web/web-main/chapter";
    }

    // template
    @GetMapping("/series")
    public String series(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "", required = false) String genre,
            @RequestParam(defaultValue = "ALL", required = false) String status,
            @RequestParam(defaultValue = "ALL", required = false) String type,
            @RequestParam(defaultValue = "lastUpdated", required = false) String orderBy,
            @RequestParam(defaultValue = "", required = false) String query,
            Model model) {
        List<Genre> genres = comicService.getAllGenres();
        Page<Comic> comicPage;
        if (query != null && !query.trim().isEmpty()) {
            List<Comic> searchResults = comicService.findByTitleContainingIgnoreCase(query.trim());
            int start = page * size;
            int end = Math.min(start + size, searchResults.size());
            List<Comic> pagedResults = start < searchResults.size() ? searchResults.subList(start, end) : List.of();
            comicPage = new PageImpl<>(pagedResults, PageRequest.of(page, size), searchResults.size());
        } else {
            comicPage = comicService.getComics(genre, status, type, orderBy, page, size);
        }
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();
        model.addAttribute("comics", comicPage.getContent());
        model.addAttribute("currentPage", comicPage.getNumber());
        model.addAttribute("totalPages", comicPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("genres", genres);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedOrderBy", orderBy);
        model.addAttribute("query", query);
        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);
        model.addAttribute("statuses", Arrays.asList(ComicStatus.values()));
        model.addAttribute("types", Arrays.asList(ComicType.values()));
        return "web/web-templates/series";
    }

    @GetMapping("/bookmarks")
    public String bookmarks(Model model) {
        // Lấy danh sách top 10 cho tuần và tháng
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();

        // Lấy danh sách thể loại
        List<Genre> genres = genreRepository.findAll();

        // Lấy danh sách ComicStatus và ComicType từ enum
        List<String> statuses = Arrays.asList("ALL", "ONGOING", "COMPLETED");
        List<String> types = Arrays.asList("ALL", "MANHWA", "MANHUA", "MANGA");

        // Thêm vào model để hiển thị trên view
        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);
        model.addAttribute("genres", genres);
        model.addAttribute("statuses", statuses);
        model.addAttribute("types", types);

        return "web/web-templates/bookmarks";
    }

    @GetMapping("/report")
    public String report(){
        return "web/web-templates/report";
    }

    @GetMapping("/authors/{slug}")
    public String authorProfile(@PathVariable String slug,
                                @RequestParam(defaultValue = "0") int page,
                                Model model) {
        // Lấy thông tin tác giả
        Author author = authorService.getAuthorBySlug(slug);
        if (author == null) {
            return "error/404";
        }

        // Lấy danh sách truyện với phân trang
        int pageSize = 10;
        Page<Comic> comicPage = authorService.getComicsByAuthorSlug(slug, page, pageSize);

        // Thêm dữ liệu vào model
        model.addAttribute("author", author);
        model.addAttribute("comics", comicPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", comicPage.getTotalPages());
        model.addAttribute("totalItems", comicPage.getTotalElements());

        return "web/web-templates/author-template";
    }

    @GetMapping("/artist/{slug}")
    public String artistProfile(@PathVariable String slug,
                                @RequestParam(defaultValue = "0") int page,
                                Model model) {
        // Lấy thông tin họa sĩ
        Artist artist = artistService.getArtistBySlug(slug);
        if (artist == null) {
            return "error/404";
        }

        // Lấy danh sách truyện với phân trang (dựa trên họa sĩ)
        int pageSize = 10;
        Page<Comic> comicPage = artistService.getComicsByArtistSlug(slug, page, pageSize);

        // Thêm dữ liệu vào model
        model.addAttribute("artist", artist); // Sử dụng "artist" thay vì "author"
        model.addAttribute("comics", comicPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", comicPage.getTotalPages());
        model.addAttribute("totalItems", comicPage.getTotalElements());

        return "web/web-templates/artist"; // Có thể đổi tên template nếu cần (xem bước 4)
    }

    // chân trang
    @GetMapping("/privacy-policy")
    public String privacyPolicy(){
        return "web/web-footer/privacy-policy";
    }

    @GetMapping("/dmcs-notice")
    public String dmcsNotice(){
        return "web/web-footer/dmcs-notice";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService(){
        return "web/web-footer/terms-of-service";
    }

    // API tìm kiếm truyện theo tiêu đề
    @GetMapping("/api/search")
    @ResponseBody
    public List<SearchComicDTO> searchComicsApi(@RequestParam("query") String query) {
        // Xóa khoảng trắng đầu cuối của từ khóa tìm kiếm để đảm bảo tính chính xác
        String trimmedQuery = query.trim();

        // Gọi ComicService để lấy danh sách truyện có tiêu đề chứa từ khóa (không phân biệt hoa thường)
        List<Comic> comics = comicService.findByTitleContainingIgnoreCase(trimmedQuery);

        // Chuyển đổi danh sách Comic thành danh sách SearchComicDTO để trả về dữ liệu tối ưu
        return comics.stream()
                .map(comic -> {
                    // Tạo đối tượng SearchComicDTO mới
                    SearchComicDTO dto = new SearchComicDTO();
                    // Gán ID của truyện
                    dto.setId(comic.getId());
                    // Gán tiêu đề của truyện
                    dto.setTitle(comic.getTitle());
                    // Gán slug của truyện (dùng để tạo URL)
                    dto.setSlug(comic.getSlug());
                    // Gán URL ảnh bìa của truyện
                    dto.setCoverImage(comic.getCoverImage());
                    // Trả về DTO đã được điền dữ liệu
                    return dto;
                })
                // Thu thập kết quả thành danh sách
                .collect(Collectors.toList());
    }

}
