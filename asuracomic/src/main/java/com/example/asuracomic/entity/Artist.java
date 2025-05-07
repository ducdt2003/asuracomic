package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "artists", indexes = @Index(name = "idx_slug", columnList = "slug", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của họa sĩ, dùng để tham chiếu trong bảng comic_artists

    @Column(nullable = false, length = 255)
    private String name; // Tên họa sĩ, hiển thị trên trang chi tiết truyện, liên kết đến trang họa sĩ

    @Column(unique = true, nullable = false, length = 255)
    private String slug; // Chuỗi thân thiện với URL (VD: tetsuya-nomura), dùng để tạo link trang họa sĩ

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm thêm họa sĩ, dùng để quản lý hoặc theo dõi

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<ComicArtist> comicArtists; // Danh sách truyện của họa sĩ, hiển thị trên trang họa sĩ
}
