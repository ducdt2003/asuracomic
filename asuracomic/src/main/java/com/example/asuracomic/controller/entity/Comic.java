package com.example.asuracomic.controller.entity;

import com.example.asuracomic.controller.model.enums.ComicStatus;
import com.example.asuracomic.controller.model.enums.ComicType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comics", indexes = @Index(name = "idx_slug", columnList = "slug", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của truyện, dùng để tham chiếu trong các bảng khác (VD: chương, đánh giá)

    @Column(nullable = false, length = 255)
    private String title; // Tiêu đề truyện, hiển thị trên danh sách truyện, trang chi tiết, và tìm kiếm

    @Column(unique = true, nullable = false, length = 255)
    private String slug; // Chuỗi thân thiện với URL (VD: one-piece), dùng để tạo link truy cập, tối ưu SEO

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả truyện, hiển thị trên trang chi tiết, cung cấp thông tin tổng quan

    @Column(length = 255)
    private String coverImage; // Đường dẫn ảnh bìa, hiển thị trên danh sách truyện, trang chi tiết, thu hút người đọc

    @Column(nullable = false)
    private Long viewCount = 0L; // Số lượt xem, dùng để xếp hạng truyện phổ biến, hiển thị trên giao diện

    @Column(nullable = false)
    private Long followCount = 0L; // Số người theo dõi, hiển thị mức độ yêu thích, hỗ trợ thông báo chương mới

    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal averageRating = BigDecimal.ZERO; // Điểm đánh giá trung bình (VD: 4.5/5), tính từ ratings, hiển thị trên trang chi tiết

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComicStatus status = ComicStatus.ONGOING; // Trạng thái truyện: ONGOING hoặc COMPLETED, hiển thị để người dùng biết tiến độ

    @Enumerated(EnumType.STRING)
    @Column
    private ComicType type = ComicType.MANHWA; // Loại truyện (MANHWA, MANGA, MANHUA), hỗ trợ lọc và phân loại truyện

    @Column(nullable = false)
    private boolean isPublished = true; // Trạng thái xuất bản: true (hiển thị công khai), false (ẩn), kiểm soát hiển thị trên web

    @Column(length = 255)
    private String serialization; // Thông tin serialization (VD: nhà xuất bản, tạp chí), hiển thị trong chi tiết truyện

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm tạo truyện, dùng để thống kê truyện mới hoặc quản lý nội dung

    @Column(nullable = false)
    private LocalDateTime updatedAt; // Thời điểm cập nhật truyện, theo dõi thay đổi (VD: thêm chương, chỉnh sửa mô tả)

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL)
    private List<Chapter> chapters; // Danh sách chương, hiển thị trên trang chi tiết truyện, hỗ trợ điều hướng đọc

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL)
    private List<ComicGenre> comicGenres; // Danh sách thể loại của truyện, hỗ trợ lọc và hiển thị thẻ thể loại

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL)
    private List<ComicAuthor> comicAuthors; // Danh sách tác giả, hiển thị trên trang chi tiết, liên kết đến trang tác giả

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL)
    private List<ComicArtist> comicArtists; // Danh sách họa sĩ, hiển thị trên trang chi tiết, liên kết đến trang họa sĩ

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL)
    private List<Favorite> favorites; // Danh sách người dùng thêm truyện vào yêu thích, hỗ trợ cá nhân hóa

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL)
    private List<Rating> ratings; // Danh sách đánh giá, dùng để tính averageRating, hiển thị mức độ chất lượng

    @OneToMany(mappedBy = "comic", cascade = CascadeType.ALL)
    private List<ComicView> comicViews; // Lịch sử lượt xem, dùng để thống kê và tăng viewCount
}
