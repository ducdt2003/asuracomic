package com.example.asuracomic.dto;

import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComicDetailDTO {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String coverImage;
    private Long viewCount;
    private Long followCount;
    private BigDecimal averageRating;
    private ComicStatus status;
    private ComicType type;
    private String serialization;
    private LocalDateTime updatedAt;
    private List<String> genres;
    private List<String> authors;
    private List<String> artists;
}