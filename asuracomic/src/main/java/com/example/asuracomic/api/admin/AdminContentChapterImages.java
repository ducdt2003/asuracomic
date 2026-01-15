package com.example.asuracomic.api.admin;

import com.example.asuracomic.entity.ChapterImage;
import com.example.asuracomic.repository.ChapterImageRepository;
import com.example.asuracomic.repository.ChapterRepository;
import com.example.asuracomic.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asura/admin/content/chapter-images")
@RequiredArgsConstructor
public class AdminContentChapterImages {

    // chưa áp dụng vào đâu cả
    private final ChapterImageRepository chapterImageRepository;

    @DeleteMapping("/delete-image/{imageId}")
    @ResponseBody
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        try {
            ChapterImage image = chapterImageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh"));

            // Lưu lại chapterId để sắp xếp lại thứ tự sau khi xóa (nếu muốn)
            Long chapterId = image.getChapter().getId();

            // Xóa ảnh khỏi Database
            chapterImageRepository.delete(image);

            // (Tùy chọn) Logic sắp xếp lại thứ tự orderIndex cho các ảnh còn lại
            reorderImages(chapterId);

            return ResponseEntity.ok("Xóa trang thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Hàm phụ để đánh lại số trang 1, 2, 3 sau khi có trang bị xóa
    private void reorderImages(Long chapterId) {
        List<ChapterImage> images = chapterImageRepository.findByChapterIdOrderByOrderIndexAsc(chapterId);
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setOrderIndex(i + 1);
        }
        chapterImageRepository.saveAll(images);
    }
}
