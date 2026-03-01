package com.example.asuracomic.api;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.repository.ChapterRepository;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.service.CoinService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/coin")
@RequiredArgsConstructor
public class CoinApi {

    private final CoinService coinService;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;
    private final HttpSession session;

    @PostMapping("/unlock-chapter")
    public ResponseEntity<?> unlockChapter(@RequestParam Long chapterId) {

        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Bạn chưa đăng nhập"));
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter không tồn tại"));

        // check coin
        if (user.getCoinBalance().compareTo(chapter.getCoinPrice()) < 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Số dư coin không đủ"));
        }

        coinService.unlockChapter(chapter.getId());

        return ResponseEntity.ok(
                Map.of(
                        "message", "Mở khóa chương thành công",
                        "chapterId", chapter.getId(),
                        "coinBalance", user.getCoinBalance()
                )
        );
    }
}
