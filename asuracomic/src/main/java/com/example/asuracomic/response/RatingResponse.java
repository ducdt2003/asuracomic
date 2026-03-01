package com.example.asuracomic.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponse {
    private Long comicId;
    private Integer score;
    private float averageRating;
}