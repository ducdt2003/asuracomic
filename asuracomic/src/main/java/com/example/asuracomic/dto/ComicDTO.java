package com.example.asuracomic.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class ComicDTO {
    private String title;
    private String slug;
    private String coverImage;
    private String status;
    private String type;
    private Integer latestChapterNumber;
    private String latestChapterTitle;
    private String latestChapterSlug;
    private float averageRating;
}
