package com.example.asuracomic.api;

import com.example.asuracomic.dto.ComicCarouselDTO;
import com.example.asuracomic.dto.ComicTopDTO;
import com.example.asuracomic.dto.RelatedComicDTO;
import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.entity.Comment;
import com.example.asuracomic.model.enums.CommentStatus;
import com.example.asuracomic.repository.CommentRepository;
import com.example.asuracomic.repository.UnlockedChapterRepository;
import com.example.asuracomic.service.ComicService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeApi {

    private final ComicService comicService;
    private final UnlockedChapterRepository unlockedChapterRepository;

    private final CommentRepository commentRepository;

    @GetMapping
    public ResponseEntity<?> homeLogin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {

        // top comics rating
        List<ComicCarouselDTO> hotComics = comicService.getHotComicsForCarousel();

        // top comics views today
        List<Comic> popularToday = comicService.getTopViewedComicsToday(5);

        // bảng xếp hạng
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();

        Page<Comic> comicPage = comicService.getComicPage(page, size);

        Object currentUser = session.getAttribute("currentUser");

        boolean isVip = false;
        List<Long> unlockedChapterIds = new ArrayList<>();

        if (currentUser != null) {
            UserDTO userDTO = (UserDTO) currentUser;

            if (userDTO.isVip() && userDTO.getVipExpireAt() != null
                    && userDTO.getVipExpireAt().isAfter(LocalDateTime.now())) {
                isVip = true;
            }

            unlockedChapterIds =
                    unlockedChapterRepository.findChapterIdsByUserId(userDTO.getId());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("hotComics", hotComics);
        response.put("popularToday", popularToday);
        response.put("top10Weekly", top10Weekly);
        response.put("top10Monthly", top10Monthly);
        response.put("comics", comicPage.getContent());
        response.put("totalPages", comicPage.getTotalPages());
        response.put("currentPage", page);
        response.put("isVip", isVip);
        response.put("unlockedChapterIds", unlockedChapterIds);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/comic/{slug}")
    public ResponseEntity<?> detail(@PathVariable String slug,
                                    @RequestParam(required = false) String redirectToAuthor,
                                    HttpSession session) {

        // Lấy chi tiết truyện
        Comic comicDetail = comicService.getComicDetailsBySlug(slug);

        // Lấy danh sách chương
        List<Chapter> chapters = comicService.getChaptersByComic(comicDetail);
        Chapter firstChapter = comicService.getFirstChapter(comicDetail);
        Chapter latestChapter = comicService.getLatestChapter(comicDetail);

        // Lấy truyện liên quan
        List<RelatedComicDTO> relatedComics =
                comicService.getRelatedComics(comicDetail.getId(), 5);

        // bảng xếp hạng
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();

        Object currentUser = session.getAttribute("currentUser");

        boolean isLoggedIn = currentUser != null;
        List<Long> unlockedChapterIds = new ArrayList<>();

        if (currentUser != null) {
            Long userId = ((UserDTO) currentUser).getId();
            unlockedChapterIds =
                    unlockedChapterRepository.findChapterIdsByUserId(userId);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("comic", comicDetail);
        response.put("chapters", chapters);
        response.put("firstChapter", firstChapter);
        response.put("latestChapter", latestChapter);
        response.put("relatedComics", relatedComics);
        response.put("top10Weekly", top10Weekly);
        response.put("top10Monthly", top10Monthly);
        response.put("isLoggedIn", isLoggedIn);
        response.put("unlockedChapterIds", unlockedChapterIds);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/comic/{comicSlug}/chapter/{chapterSlug}")
    public ResponseEntity<?> chapter(@PathVariable String comicSlug,
                                     @PathVariable String chapterSlug,
                                     @RequestParam(defaultValue = "0") int commentPage,
                                     @RequestParam(defaultValue = "10") int commentSize,
                                     HttpSession session) {

        // Lấy truyện
        Comic comic = comicService.getComicDetailsBySlug(comicSlug);
        if (comic == null) {
            return ResponseEntity.badRequest().body("Comic không tồn tại");
        }

        // Lấy chương
        Chapter chapter = comicService.getChapterBySlug(comic, chapterSlug);
        if (chapter == null) {
            return ResponseEntity.badRequest().body("Chapter không tồn tại");
        }

        // Truyện liên quan
        List<RelatedComicDTO> relatedComics =
                comicService.getRelatedComics(comic.getId(), 5);

        // Sắp xếp chương
        List<Chapter> sortedChapters = comic.getChapters().stream()
                .filter(Chapter::isPublished)
                .sorted(Comparator.comparingInt(Chapter::getChapterNumber))
                .collect(Collectors.toList());

        // Tìm chapter trước / sau
        Chapter previousChapter = null;
        Chapter nextChapter = null;

        for (int i = 0; i < sortedChapters.size(); i++) {
            Chapter current = sortedChapters.get(i);

            if (current.getId().equals(chapter.getId())) {

                if (i > 0) {
                    previousChapter = sortedChapters.get(i - 1);
                }

                if (i < sortedChapters.size() - 1) {
                    nextChapter = sortedChapters.get(i + 1);
                }

                break;
            }
        }

        // Lấy comments
        List<Comment> allComments =
                commentRepository.findByChapterAndStatusOrderByCreatedAtDesc(
                        chapter, CommentStatus.ACTIVE
                );

        int start = commentPage * commentSize;
        int end = Math.min(start + commentSize, allComments.size());

        List<Comment> pagedComments =
                start < allComments.size()
                        ? allComments.subList(start, end)
                        : List.of();

        Page<Comment> commentPageData =
                new PageImpl<>(pagedComments,
                        PageRequest.of(commentPage, commentSize),
                        allComments.size());

        // Login check
        Object currentUser = session.getAttribute("currentUser");

        boolean isLoggedIn = currentUser != null;
        Long currentUserId = null;

        List<Long> unlockedChapterIds = new ArrayList<>();

        if (currentUser != null) {

            UserDTO userDTO = (UserDTO) currentUser;
            currentUserId = userDTO.getId();

            unlockedChapterIds =
                    unlockedChapterRepository.findChapterIdsByUserId(currentUserId);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("comic", comic);
        response.put("chapter", chapter);
        response.put("relatedComics", relatedComics);
        response.put("sortedChapters", sortedChapters);
        response.put("previousChapter", previousChapter);
        response.put("nextChapter", nextChapter);

        response.put("comments", commentPageData.getContent());
        response.put("commentPage", commentPageData.getNumber());
        response.put("totalCommentPages", commentPageData.getTotalPages());
        response.put("commentSize", commentSize);

        response.put("currentUserId", currentUserId);
        response.put("isLoggedIn", isLoggedIn);
        response.put("unlockedChapterIds", unlockedChapterIds);

        return ResponseEntity.ok(response);
    }
}
