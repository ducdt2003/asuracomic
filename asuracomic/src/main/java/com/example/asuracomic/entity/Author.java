package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "authors", indexes = @Index(name = "idx_slug", columnList = "slug", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của tác giả, dùng để tham chiếu trong bảng comic_authors

    @Column(nullable = false, length = 255)
    private String name; // Tên tác giả, hiển thị trên trang chi tiết truyện, liên kết đến trang tác giả

    @Column(unique = true, nullable = false, length = 255)
    private String slug; // Chuỗi thân thiện với URL (VD: eiichiro-oda), dùng để tạo link trang tác giả

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm thêm tác giả, dùng để quản lý hoặc theo dõi

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<ComicAuthor> comicAuthors; // Danh sách truyện của tác giả, hiển thị trên trang tác giả
}
