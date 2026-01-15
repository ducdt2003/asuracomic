package com.example.asuracomic.dto.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ChapterEditFormDTO {
    private String title;
    private Integer chapterNumber;
    private Boolean isFree;
    private BigDecimal coinPrice;
    private Boolean isPublished;
    private List<MultipartFile> newImages; // Để tải thêm ảnh mới
}
