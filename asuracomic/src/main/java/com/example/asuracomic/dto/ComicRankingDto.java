package com.example.asuracomic.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ComicRankingDto {
    private Long id;
    private String title;
    private String coverImage;
    private List<String> genres;
    private BigDecimal averageRating;
}