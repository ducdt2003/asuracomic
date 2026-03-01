package com.example.asuracomic.service;

import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.entity.Rating;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.model.request.RatingRequest;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.repository.RatingRepository;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.response.RatingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ComicRepository comicRepository;
    private final UserRepository userRepository;

    public RatingResponse rateComic(Long userId, RatingRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (request.getScore() < 1 || request.getScore() > 5) {
            throw new IllegalArgumentException("Điểm đánh giá phải từ 1 đến 5");
        }

        Comic comic = comicRepository.findById(request.getComicId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));

        Rating rating = ratingRepository
                .findByUserIdAndComicId(user.getId(), comic.getId())
                .orElse(null);

        if (rating == null) {
            rating = Rating.builder()
                    .user(user)
                    .comic(comic)
                    .score(request.getScore())
                    .createdAt(LocalDateTime.now())
                    .build();
        } else {
            rating.setScore(request.getScore());
        }

        ratingRepository.save(rating);

        Double avg = ratingRepository.calculateAverageRating(comic.getId());
        comic.setAverageRating(BigDecimal.valueOf(avg));
        comicRepository.save(comic);

        return RatingResponse.builder()
                .comicId(comic.getId())
                .score(rating.getScore())
                .averageRating(avg.floatValue())
                .build();
    }
}