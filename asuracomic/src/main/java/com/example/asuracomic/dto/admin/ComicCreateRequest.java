package com.example.asuracomic.dto.admin;

import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import lombok.Data;

import java.util.List;

@Data
public class ComicCreateRequest {
    private String title;
    private String description;
    private String coverImage;
    private ComicStatus status;
    private ComicType type;
    private String serialization;
    List<Long> genreIds;
    List<Long> authorIds;
    List<Long> artistIds;
}
