package com.example.asuracomic.dto.admin;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ChapterDTO {
    private Long id;
    private String comicTitle;
    private Integer chapterNumber;
    private String title;
    private BigDecimal coinPrice;
    private boolean isPublished;
}
