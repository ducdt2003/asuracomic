package com.example.asuracomic.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComicUpdateForm {
    private String title;
    private String slug;
    private String coverImage;
    private MultipartFile coverImageFile; // Thêm để nhận file từ upload
    private String description;
    private String serialization;
    private String status; // ONGOING, COMPLETED
    private String type;   // MANHWA, MANGA, MANHUA
    private boolean isPublished;
    private List<Long> genreIds;
    private List<Long> authorIds;
    private List<Long> artistIds;
}
