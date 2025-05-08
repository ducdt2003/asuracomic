package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chapters", indexes = {
        @Index(name = "idx_comic_id_chapter_number", columnList = "comic_id, chapter_number", unique = true),
        @Index(name = "idx_comic_id_slug", columnList = "comic_id, slug", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của chương, dùng để tham chiếu trong các bảng khác (VD: ảnh, bình luận)

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic; // Truyện mà chương thuộc về, liên kết để hiển thị chương trong trang chi tiết truyện

    @Column(nullable = false, length = 255)
    private String title; // Tiêu đề chương, hiển thị trên danh sách chương và khi đọc

    @Column(nullable = false, length = 255)
    private String slug; // Chuỗi thân thiện với URL (VD: chapter-100), dùng để tạo link truy cập, tối ưu SEO

    @Column(nullable = false)
    private Integer chapterNumber; // Số thứ tự chương (VD: 1, 2, 100), dùng để sắp xếp và hiển thị

    @Column(nullable = false)
    private boolean isFree = false; // Trạng thái miễn phí: true (miễn phí), false (phải trả coin), kiểm soát quyền truy cập

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal coinPrice = BigDecimal.ZERO; // Giá coin để mở khóa (VD: 5.00), hiển thị trên giao diện

    @Column(nullable = false)
    private Long viewCount = 0L; // Số lượt xem chương, dùng để thống kê độ phổ biến, hiển thị trên giao diện

    @Column(nullable = false)
    private boolean isPublished = true; // Trạng thái xuất bản: true (hiển thị), false (ẩn), kiểm soát hiển thị chương

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm tạo chương, dùng để thống kê chương mới hoặc quản lý

    @Column(nullable = false)
    private LocalDateTime updatedAt; // Thời điểm cập nhật chương, theo dõi thay đổi (VD: chỉnh sửa tiêu đề, ảnh)

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    private List<ChapterImage> chapterImages; // Danh sách ảnh của chương, hiển thị nội dung khi người dùng đọc

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    private List<UnlockedChapter> unlockedChapters; // Danh sách người dùng đã mở khóa chương, kiểm tra quyền truy cập

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    private List<Comment> comments; // Danh sách bình luận về chương, hiển thị phần thảo luận dưới trang đọc
}
