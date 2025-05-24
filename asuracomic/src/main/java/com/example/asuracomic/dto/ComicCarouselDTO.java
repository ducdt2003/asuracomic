package com.example.asuracomic.dto;

import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ComicCarouselDTO {
    private Long id;
    private String title;
    private ComicType type;
    private String genres; // ví dụ: "Action, Adventure, Fantasy"
    private BigDecimal averageRating;
    private String summary;
    private ComicStatus status;
    private String coverImage;
    private String author;
    private String slug;
}