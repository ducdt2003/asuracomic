package com.example.asuracomic.model.request;

import lombok.Data;

@Data
public class RatingRequest {
    private Long comicId;
    private Integer score; // 1 - 5
}