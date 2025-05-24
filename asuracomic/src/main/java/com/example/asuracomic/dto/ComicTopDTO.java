package com.example.asuracomic.dto;

import lombok.Data;

@Data
public class ComicTopDTO {
    private Long id;
    private String title;
    private String coverImage;
    private Double averageRating;
    private Long viewCount;
    private String genres;
    private Double combinedScore;
    private String slug;

    public ComicTopDTO(Long id, String title, String coverImage, Double averageRating, Long viewCount, String genres, Double combinedScore, String slug) {
        this.id = id;
        this.title = title;
        this.coverImage = coverImage;
        this.averageRating = averageRating;
        this.viewCount = viewCount;
        this.genres = genres;
        this.combinedScore = combinedScore;
        this.slug = slug;
    }
}