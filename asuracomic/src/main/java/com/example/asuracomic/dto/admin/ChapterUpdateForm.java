package com.example.asuracomic.dto.admin;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ChapterUpdateForm {
    private String title;
    private Integer chapterNumber;
    private Boolean isFree;
    private BigDecimal coinPrice;
    private Boolean isPublished;
    private List<MultipartFile> images;
}
