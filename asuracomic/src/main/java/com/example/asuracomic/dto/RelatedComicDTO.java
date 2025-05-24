package com.example.asuracomic.dto;

import com.example.asuracomic.model.enums.ComicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatedComicDTO {
    private Long id;
    private String title;
    private String slug;
    private String coverImage;
    private ComicStatus status;
    private Integer latestChapterNumber;
    private BigDecimal averageRating;
}