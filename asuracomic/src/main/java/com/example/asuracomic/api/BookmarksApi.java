package com.example.asuracomic.api;


import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.exception.CustomException;
import com.example.asuracomic.service.ComicService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/asura/api/bookmarks")
@RequiredArgsConstructor
public class BookmarksApi {

    private final ComicService comicService;

    @PostMapping("/{comicId}")
    public String addToBookmarks(@PathVariable Long comicId, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId(session);
            if (userId == null) {
                return "login_required";
            }
            comicService.addToFavorites(userId, comicId);
            return "added";
        } catch (CustomException e) {
            if (e.getMessage().contains("already exists")) {
                return "already_exists";
            }
            return "error";
        }
    }

    private Long getCurrentUserId(HttpSession session) {
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            return null;
        }
        return ((UserDTO) currentUser).getId();
    }

    @DeleteMapping("/{comicId}")
    public String removeFromBookmarks(@PathVariable Long comicId, HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            if (userId == null) {
                return "login_required";
            }
            comicService.removeFromFavorites(userId, comicId);
            return "removed";
        } catch (CustomException e) {
            return "error";
        }
    }
}