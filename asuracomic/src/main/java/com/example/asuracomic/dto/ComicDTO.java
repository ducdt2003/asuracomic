package com.example.asuracomic.dto;

import com.example.asuracomic.model.enums.ComicType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComicDTO {
    private String title;
    private String slug;
    private String coverImage;
    private ComicType type;
    private Integer latestChapterNumber;
    private BigDecimal averageRating;
}