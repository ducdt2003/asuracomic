package com.example.asuracomic.dto.admin;

import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter // Tạo getter cho tất cả thuộc tính
@Setter // Tạo setter cho tất cả thuộc tính
public class MangaDTO { // Lớp DTO để truyền dữ liệu truyện (đổi tên từ ComicDTO)
    private Long id; // ID truyện
    private String title; // Tiêu đề truyện
    private ComicStatus status; // Trạng thái truyện (ONGOING, COMPLETED)
    private ComicType type; // Loại truyện (MANHWA, MANGA, MANHUA)
    private Long viewCount; // Lượt xem
}
