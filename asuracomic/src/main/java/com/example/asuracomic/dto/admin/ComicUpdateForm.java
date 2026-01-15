package com.example.asuracomic.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComicUpdateForm {
    private String title;
    private String slug;
    private String coverImage;
    private String description;
    private String serialization;
    private String status; // ONGOING, COMPLETED
    private String type;   // MANHWA, MANGA, MANHUA
    private boolean isPublished;
    private List<Long> genreIds;
    private List<Long> authorIds;
    private List<Long> artistIds;
}
