package com.example.asuracomic.service.admin.admincomic;

import com.example.asuracomic.dto.admin.ChapterEditFormDTO;
import com.example.asuracomic.dto.admin.ChapterUpdateForm;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.ChapterImage;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.repository.ChapterImageRepository;
import com.example.asuracomic.repository.ChapterRepository;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.service.admin.adminuser.CloudinaryService;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminChapterService {
    private final ComicRepository comicRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterImageRepository chapterImageRepository;
    private final CloudinaryService cloudinaryService;
    private final Slugify slugify = Slugify.builder().build();

    // thêm chương
    @Transactional
    public void createChapter(Long comicId, ChapterUpdateForm form) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));

        // 1. Tạo đối tượng Chapter
        Chapter chapter = Chapter.builder()
                .comic(comic)
                .title(form.getTitle())
                .chapterNumber(form.getChapterNumber())
                // Tạo slug ví dụ: chapter-5-tieu-de-chuong
                .slug(slugify.slugify("chapter-" + form.getChapterNumber() + "-" + form.getTitle()))
                .isFree(form.getIsFree() != null && form.getIsFree())
                .coinPrice(form.getCoinPrice())
                .isPublished(form.getIsPublished() != null && form.getIsPublished())
                .viewCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Chapter savedChapter = chapterRepository.save(chapter);

        // 2. Xử lý Upload danh sách ảnh truyện
        if (form.getImages() != null && !form.getImages().isEmpty()) {
            int orderIndex = 1;
            for (MultipartFile file : form.getImages()) {
                if (!file.isEmpty()) {
                    // Gọi service Cloudinary bạn đã viết
                    String imageUrl = cloudinaryService.uploadFile(file);

                    // Lưu vào bảng chapter_images
                    ChapterImage chapterImage = ChapterImage.builder()
                            .chapter(savedChapter)
                            .imageUrl(imageUrl)
                            .orderIndex(orderIndex++) // Tự động tăng thứ tự trang 1, 2, 3...
                            .createdAt(LocalDateTime.now())
                            .build();
                    chapterImageRepository.save(chapterImage);
                }
            }
        }
    }


    // 1. Lấy chapter kèm ảnh để hiển thị lên form
    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương"));
    }

    // sữa chương
  /*  @Transactional
    public void updateChapter(Long chapterId, ChapterEditFormDTO form) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương"));

        chapter.setTitle(form.getTitle());
        chapter.setChapterNumber(form.getChapterNumber());
        chapter.setSlug(slugify.slugify("chapter-" + form.getChapterNumber() + "-" + form.getTitle()));

        // ✅ SỬA Ở ĐÂY
        chapter.setFree(Boolean.TRUE.equals(form.getIsFree()));
        chapter.setCoinPrice(form.getCoinPrice());
        chapter.setPublished(Boolean.TRUE.equals(form.getIsPublished()));

        chapter.setUpdatedAt(LocalDateTime.now());

        // Upload thêm ảnh
        if (form.getNewImages() != null && !form.getNewImages().isEmpty()) {
            int currentMaxOrder = chapter.getChapterImages().stream()
                    .map(ChapterImage::getOrderIndex)
                    .max(Integer::compareTo)
                    .orElse(0);

            for (MultipartFile file : form.getNewImages()) {
                if (!file.isEmpty()) {
                    String url = cloudinaryService.uploadFile(file);
                    ChapterImage chapterImage = ChapterImage.builder()
                            .chapter(chapter)
                            .imageUrl(url)
                            .orderIndex(++currentMaxOrder)
                            .createdAt(LocalDateTime.now())
                            .build();
                    chapterImageRepository.save(chapterImage);
                }
            }
        }

        chapterRepository.save(chapter);
    }*/
    @Transactional
    public void updateChapter(Long chapterId, ChapterEditFormDTO form) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương"));

        // 1. Cập nhật thông tin cơ bản
        chapter.setTitle(form.getTitle());
        chapter.setChapterNumber(form.getChapterNumber());
        chapter.setSlug(slugify.slugify("chapter-" + form.getChapterNumber() + "-" + form.getTitle()));
        chapter.setFree(Boolean.TRUE.equals(form.getIsFree()));
        chapter.setCoinPrice(form.getCoinPrice());
        chapter.setPublished(Boolean.TRUE.equals(form.getIsPublished()));
        chapter.setUpdatedAt(LocalDateTime.now());

        // 2. Xử lý tải ảnh mới lên
        if (form.getNewImages() != null && !form.getNewImages().isEmpty()) {
            // Lấy số thứ tự lớn nhất hiện có
            int currentMaxOrder = chapter.getChapterImages().stream()
                    .map(ChapterImage::getOrderIndex)
                    .max(Integer::compareTo)
                    .orElse(0);

            for (MultipartFile file : form.getNewImages()) {
                if (!file.isEmpty()) {
                    String url = cloudinaryService.uploadFile(file);
                    ChapterImage chapterImage = ChapterImage.builder()
                            .chapter(chapter)
                            .imageUrl(url)
                            .orderIndex(++currentMaxOrder) // Tăng dần để nối tiếp vào sau
                            .createdAt(LocalDateTime.now())
                            .build();
                    chapterImageRepository.save(chapterImage);
                }
            }
        }

        // 3. Quan trọng: Đánh lại số thứ tự (Reorder) toàn bộ ảnh để tránh bị đứt quãng trang (1, 3, 4...)
        reorderImages(chapterId);

        chapterRepository.save(chapter);
    }

    private void reorderImages(Long chapterId) {
        List<ChapterImage> images = chapterImageRepository.findByChapterIdOrderByOrderIndexAsc(chapterId);
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setOrderIndex(i + 1);
        }
        chapterImageRepository.saveAll(images);
    }

    // xóa nội dung chpater
    @Transactional
    public Long deleteImageAndReorder(Long imageId) {
        // 1. Lấy ảnh
        ChapterImage image = chapterImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh"));

        Long chapterId = image.getChapter().getId();

        // 2. Xóa ảnh
        chapterImageRepository.delete(image);

        // 3. Lấy lại danh sách ảnh theo thứ tự
        List<ChapterImage> images =
                chapterImageRepository.findByChapterIdOrderByOrderIndexAsc(chapterId);

        // 4. Đánh lại số trang (1,2,3...)
        int index = 1;
        for (ChapterImage img : images) {
            img.setOrderIndex(index++);
        }

        chapterImageRepository.saveAll(images);

        return chapterId;
    }
    // thêm nội dung choater
    @Transactional
    public void addImagesToChapter(Long chapterId, List<MultipartFile> images) {

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chapter"));

        int maxOrder = chapterImageRepository
                .findByChapterIdOrderByOrderIndexAsc(chapterId)
                .stream()
                .map(ChapterImage::getOrderIndex)
                .max(Integer::compareTo)
                .orElse(0);

        for (MultipartFile file : images) {
            if (file.isEmpty()) continue;

            String imageUrl = cloudinaryService.uploadFile(file);

            ChapterImage image = ChapterImage.builder()
                    .chapter(chapter)
                    .imageUrl(imageUrl)
                    .orderIndex(++maxOrder)
                    .createdAt(LocalDateTime.now())
                    .build();

            chapterImageRepository.save(image);
        }
    }





}
