package com.example.asuracomic.dto;

import lombok.Data;

@Data
public class SearchComicDTO {
    private Long id;
    private String title;
    private String slug;
    private String coverImage;
}