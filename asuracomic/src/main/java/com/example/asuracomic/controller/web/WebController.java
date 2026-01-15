package com.example.asuracomic.controller.web;

import com.example.asuracomic.dto.*;
import com.example.asuracomic.entity.*;
import com.example.asuracomic.exception.CustomException;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import com.example.asuracomic.model.enums.CommentStatus;
import com.example.asuracomic.model.request.ChangePasswordRequest;
import com.example.asuracomic.repository.CommentRepository;
import com.example.asuracomic.repository.GenreRepository;
import com.example.asuracomic.repository.UnlockedChapterRepository;
import com.example.asuracomic.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final UnlockedChapterRepository unlockedChapterRepository;
    private final HttpSession session;
    private final UserService userService;


    // trang chủ pramsau dấu hỏu
    @GetMapping
    public String homeLogin(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        // top comics rating lưu vào biến hco
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
        model.addAttribute("totalPages", comicPage.getTotalPages()); // ttoongr số lượng trang để vẻ ra 123  100/30==> 3
        model.addAttribute("currentPage", page); // số trang hiện taij ngươi dung đangở đâu


        // Lấy danh sách chương đã mở khóa nếu có người dùng đăng nhập
        Object currentUser = session.getAttribute("currentUser");
        boolean isVip = false;

        if (currentUser != null) {
            UserDTO userDTO = (UserDTO) currentUser;

            // Kiểm tra VIP còn hạn
            if (userDTO.isVip() && userDTO.getVipExpireAt() != null
                    && userDTO.getVipExpireAt().isAfter(LocalDateTime.now())) {
                isVip = true;
            }

            List<Long> unlockedChapterIds = unlockedChapterRepository.findChapterIdsByUserId(userDTO.getId());
            model.addAttribute("unlockedChapterIds", unlockedChapterIds);
        }

        model.addAttribute("isVip", isVip);

        return "web/web-main/home";
    }

    // trang chi tiết tền tham số lêurl
    @GetMapping("/comic/{slug}")
    public String detail(@PathVariable String slug,
                         @RequestParam(required = false) String redirectToAuthor,
                         Model model, HttpSession session) {
        // Lấy chi tiết truyện theo slug
        Comic comicDetail = comicService.getComicDetailsBySlug(slug);
        /*if (comicDetail == null) {
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
        }*/

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

        model.addAttribute("isLoggedIn", session.getAttribute("currentUser") != null);

        // Lấy danh sách chương đã mở khóa nếu có người dùng đăng nhập
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser != null) {
            Long userId = ((UserDTO) currentUser).getId();
            List<Long> unlockedChapterIds = unlockedChapterRepository.findChapterIdsByUserId(userId);
            model.addAttribute("unlockedChapterIds", unlockedChapterIds);
        }
        return "web/web-main/detail";
    }



    // trang chapter
    /*@GetMapping("/comic/{comicSlug}/chapter/{chapterSlug}")
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
        // Lấy danh sách bình luận cho chương này
        List<CommentResponseDTO> comments = commentService.getCommentsByChapter(chapter.getId());
        model.addAttribute("comments", comments);

        return "web/web-main/chapter";
    }*/
    @GetMapping("/comic/{comicSlug}/chapter/{chapterSlug}")
    public String chapter(@PathVariable String comicSlug, @PathVariable String chapterSlug,
                          @RequestParam(defaultValue = "0") int commentPage,
                          @RequestParam(defaultValue = "10") int commentSize,
                          Model model, HttpSession session) {

        // Lấy chi tiết truyện qua comicSlug
        Comic comic = comicService.getComicDetailsBySlug(comicSlug);
        if (comic == null) return "redirect:/error";

        // Lấy chương qua chapterSlug
        Chapter chapter = comicService.getChapterBySlug(comic, chapterSlug);
        if (chapter == null) return "redirect:/error";

        // Lấy danh sách truyện liên quan
        List<RelatedComicDTO> relatedComics = comicService.getRelatedComics(comic.getId(), 5);

        // Sắp xếp danh sách chương
        List<Chapter> sortedChapters = comic.getChapters().stream()
                .filter(Chapter::isPublished)
                .sorted(Comparator.comparingInt(Chapter::getChapterNumber))
                .collect(Collectors.toList());

        // Tìm chương trước và sau
        Chapter previousChapter = null;
        Chapter nextChapter = null;
        for (int i = 0; i < sortedChapters.size(); i++) {
            Chapter current = sortedChapters.get(i);
            if (current.getId().equals(chapter.getId())) {
                if (i > 0) previousChapter = sortedChapters.get(i - 1);
                if (i < sortedChapters.size() - 1) nextChapter = sortedChapters.get(i + 1);
                break;
            }
        }

        // BỔ SUNG: Lấy danh sách bình luận trực tiếp từ entity
        List<Comment> allComments = commentRepository.findByChapterAndStatusOrderByCreatedAtDesc(chapter, CommentStatus.ACTIVE);

        int start = commentPage * commentSize;
        int end = Math.min(start + commentSize, allComments.size());
        List<Comment> pagedComments = start < allComments.size() ? allComments.subList(start, end) : List.of();
        Page<Comment> commentPageData = new PageImpl<>(pagedComments, PageRequest.of(commentPage, commentSize), allComments.size());

        // Đẩy dữ liệu bình luận ra giao diện (sử dụng trực tiếp entity Comment)
        model.addAttribute("comments", commentPageData.getContent());
        model.addAttribute("commentPage", commentPageData.getNumber());
        model.addAttribute("totalCommentPages", commentPageData.getTotalPages());
        model.addAttribute("commentSize", commentSize);

        // Dữ liệu khác (không thay đổi)
        model.addAttribute("comic", comic);
        model.addAttribute("chapter", chapter);
        model.addAttribute("relatedComics", relatedComics);
        model.addAttribute("sortedChapters", sortedChapters);
        model.addAttribute("previousChapter", previousChapter);
        model.addAttribute("nextChapter", nextChapter);
/*        model.addAttribute("isLoggedIn", getCurrentUserId() != null);*/
        model.addAttribute("newComment", new CommentDTO());
        model.addAttribute("currentUserId", getCurrentUserId());

        model.addAttribute("isLoggedIn", session.getAttribute("currentUser") != null);


        // Lấy danh sách chương đã mở khóa nếu có người dùng đăng nhập
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser != null) {
            Long userId = ((UserDTO) currentUser).getId();
            List<Long> unlockedChapterIds = unlockedChapterRepository.findChapterIdsByUserId(userId);
            model.addAttribute("unlockedChapterIds", unlockedChapterIds);
        }

        return "web/web-main/chapter";
    }



    // Thêm bình luận mới (Form submission)
    @PostMapping("/comic/{comicSlug}/chapter/{chapterSlug}/comment")
    public String createComment(@PathVariable String comicSlug, @PathVariable String chapterSlug,
                                @Valid @ModelAttribute("newComment") CommentDTO commentDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            Comic comic = comicService.getComicDetailsBySlug(comicSlug);
            if (comic == null) {
                return "redirect:/error";
            }
            Chapter chapter = comicService.getChapterBySlug(comic, chapterSlug);
            if (chapter == null) {
                return "redirect:/error";
            }
            commentDTO.setChapterId(chapter.getId());
            commentService.createComment(userId, commentDTO);
            redirectAttributes.addFlashAttribute("message", "Bình luận đã được thêm!");
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asura/comic/" + comicSlug + "/chapter/" + chapterSlug;
    }

    // Thêm bình luận mới (AJAX) tra về json
    @PostMapping(value = "/comic/{comicSlug}/chapter/{chapterSlug}/comment/ajax", produces = "application/json")
    @ResponseBody
    public ResponseEntity<CommentResponseDTO> createCommentAjax(@PathVariable String comicSlug,
                                                                @PathVariable String chapterSlug,
                                                                @Valid @RequestBody CommentDTO commentDTO) {
        try {
            Long userId = getCurrentUserId();
            Comic comic = comicService.getComicDetailsBySlug(comicSlug);
            if (comic == null) {
                throw new CustomException("Comic not found");
            }
            Chapter chapter = comicService.getChapterBySlug(comic, chapterSlug);
            if (chapter == null) {
                throw new CustomException("Chapter not found");
            }
            commentDTO.setChapterId(chapter.getId());
            CommentResponseDTO createdComment = commentService.createComment(userId, commentDTO);
            return ResponseEntity.ok(createdComment);
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Chỉnh sửa bình luận
    @PostMapping("/comic/{comicSlug}/chapter/{chapterSlug}/comment/{commentId}/edit")
    public String editComment(@PathVariable String comicSlug, @PathVariable String chapterSlug,
                              @PathVariable Long commentId, @Valid @ModelAttribute("editComment") CommentDTO commentDTO,
                              RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            commentService.editComment(commentId, userId, commentDTO.getContent());
            redirectAttributes.addFlashAttribute("message", "Bình luận đã được chỉnh sửa!");
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asura/comic/" + comicSlug + "/chapter/" + chapterSlug;
    }

    // Xóa bình luận
    @PostMapping("/comic/{comicSlug}/chapter/{chapterSlug}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable String comicSlug, @PathVariable String chapterSlug,
                                @PathVariable Long commentId, RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            commentService.deleteComment(commentId, userId);
            redirectAttributes.addFlashAttribute("message", "Bình luận đã được xóa!");
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asura/comic/" + comicSlug + "/chapter/" + chapterSlug;
    }

    // Báo cáo bình luận
    @PostMapping("/comic/{comicSlug}/chapter/{chapterSlug}/comment/{commentId}/report")
    public String reportComment(@PathVariable String comicSlug, @PathVariable String chapterSlug,
                                @PathVariable Long commentId, @ModelAttribute ReportDTO reportDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            commentService.reportComment(commentId, userId, reportDTO.getReason());
            redirectAttributes.addFlashAttribute("message", "Báo cáo đã được gửi!");
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asura/comic/" + comicSlug + "/chapter/" + chapterSlug;
    }


    @PostMapping("/report/submit")
    public String submitReport(@RequestParam(required = false) Long commentId,
                               @RequestParam String reason,
                               @RequestParam String description,
                               @RequestParam(required = false) String comicSlug,
                               @RequestParam(required = false) String chapterSlug,
                               RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                throw new CustomException("Bạn cần đăng nhập để gửi báo cáo.");
            }
            String fullReason = reason + (description != null && !description.isEmpty() ? " - " + description : "");
            if (commentId != null) {
                commentService.reportComment(commentId, userId, fullReason);
            } else {
                throw new CustomException("Vui lòng cung cấp ID bình luận.");
            }
            redirectAttributes.addFlashAttribute("message", "Báo cáo đã được gửi thành công!");
            if (comicSlug != null && chapterSlug != null) {
                redirectAttributes.addFlashAttribute("redirectUrl", "/asura/comic/" + comicSlug + "/chapter/" + chapterSlug);
            }
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asura/report/success";
    }



    // Lấy ID người dùng hiện tại từ session
    private Long getCurrentUserId() {
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            return null;
        }
        return ((UserDTO) currentUser).getId();
    }


    @GetMapping("/report/success")
    public String showReportSuccessPage(Model model) {
        // Không cần thêm logic phức tạp vì thông báo đã được truyền qua RedirectAttributes
        return "web/web-templates/report-success";
    }





    // template
    /*@GetMapping("/series")
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
    }*/
    @GetMapping("/series")
    public String series(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "", required = false) String genre,
            @RequestParam(defaultValue = "ALL", required = false) String status,
            @RequestParam(defaultValue = "ALL", required = false) String type,
            @RequestParam(defaultValue = "lastUpdated", required = false) String orderBy,
            @RequestParam(defaultValue = "", required = false) String query, // Giữ nguyên
            Model model) {

        List<Genre> genres = comicService.getAllGenres();

        // === PHẦN SỬA ĐỔI CHÍNH ===
        // Xóa bỏ khối if-else. Gọi MỘT phương thức service xử lý tất cả.
        // Chúng ta sẽ truyền 'query' vào 'getComics'
        Page<Comic> comicPage = comicService.getComics(genre, status, type, orderBy, query, page, size);
        // ==========================

        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();

        model.addAttribute("comics", comicPage.getContent());
        model.addAttribute("currentPage", comicPage.getNumber());
        model.addAttribute("totalPages", comicPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("genres", genres);

        // Dùng các biến này để giữ trạng thái của form và cho link phân trang
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedOrderBy", orderBy);
        model.addAttribute("query", query); // Thêm query vào model

        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);
        model.addAttribute("statuses", Arrays.asList(ComicStatus.values()));
        model.addAttribute("types", Arrays.asList(ComicType.values()));
        return "web/web-templates/series";
    }

//    @GetMapping("/bookmarks")
//    public String bookmarks(Model model) {
//        // Lấy danh sách top 10 cho tuần và tháng
//        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
//        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();
//
//        // Lấy danh sách thể loại
//        List<Genre> genres = genreRepository.findAll();
//
//        // Lấy danh sách ComicStatus và ComicType từ enum
//        List<String> statuses = Arrays.asList("ALL", "ONGOING", "COMPLETED");
//        List<String> types = Arrays.asList("ALL", "MANHWA", "MANHUA", "MANGA");
//
//        // Thêm vào model để hiển thị trên view
//        model.addAttribute("top10Weekly", top10Weekly);
//        model.addAttribute("top10Monthly", top10Monthly);
//        model.addAttribute("genres", genres);
//        model.addAttribute("statuses", statuses);
//        model.addAttribute("types", types);
//
//        return "web/web-templates/bookmarks";
//    }
// Trong WebController.java
/*@GetMapping("/bookmarks")
public String bookmarks(Model model) {
    Long userId = getCurrentUserId();
    if (userId != null) {
        List<Comic> favoriteComics = comicService.getFavoriteComics(userId);
        model.addAttribute("favoriteComics", favoriteComics);
    } else {
        model.addAttribute("favoriteComics", List.of()); // Tránh lỗi khi chưa đăng nhập
    }

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
}*/

    @GetMapping("/bookmarks")
    public String bookmarks(Model model) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            List<Comic> favoriteComics = comicService.getFavoriteComics(userId);
            model.addAttribute("favoriteComics", favoriteComics);
        } else {
            model.addAttribute("favoriteComics", List.of());
        }

        model.addAttribute("top10Weekly", comicService.getTop10CombinedWeekly());
        model.addAttribute("top10Monthly", comicService.getTop10CombinedMonthly());
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("statuses", Arrays.asList("ALL", "ONGOING", "COMPLETED"));
        model.addAttribute("types", Arrays.asList("ALL", "MANHWA", "MANHUA", "MANGA"));

        return "web/web-templates/bookmarks";
    }





    @GetMapping("/report")
    public String showReportPage(@RequestParam(required = false) Long chapterId,
                                 @RequestParam(required = false) Long commentId,
                                 Model model) {
        if (getCurrentUserId() == null) {
            return "redirect:/asura/login";
        }
        if (chapterId != null) {
            model.addAttribute("chapterId", chapterId);
        }
        if (commentId != null) {
            model.addAttribute("commentId", commentId);
        }
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


    // profile user
    @GetMapping("/profile")
    public String proFile(Model model){
        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", currentUser);
        return "web/web-templates/profile";
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