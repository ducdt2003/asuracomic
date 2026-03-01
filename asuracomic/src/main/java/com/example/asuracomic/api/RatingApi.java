package com.example.asuracomic.api;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.model.request.RatingRequest;
import com.example.asuracomic.response.RatingResponse;
import com.example.asuracomic.service.RatingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingApi {

    private final RatingService ratingService;
    private final HttpSession session;

    @PostMapping
    public RatingResponse rateComic(@RequestBody RatingRequest request) {

        Object sessionUser = session.getAttribute("currentUser");
        if (sessionUser == null) {
            throw new RuntimeException("Bạn chưa đăng nhập");
        }

        if (!(sessionUser instanceof UserDTO)) {
            throw new RuntimeException("Dữ liệu session không hợp lệ");
        }

        UserDTO userDTO = (UserDTO) sessionUser;

        return ratingService.rateComic(userDTO.getId(), request);
    }
}