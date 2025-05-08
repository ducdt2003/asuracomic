package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "genres", indexes = @Index(name = "idx_slug", columnList = "slug", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của thể loại, dùng để tham chiếu trong bảng comic_genres

    @Column(unique = true, nullable = false, length = 100)
    private String name; // Tên thể loại (VD: Action, Romance), hiển thị trên giao diện, dùng để lọc truyện

    @Column(unique = true, nullable = false, length = 255)
    private String slug; // Chuỗi thân thiện với URL (VD: action), dùng để tạo link danh sách truyện theo thể loại

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm tạo thể loại, dùng để quản lý hoặc theo dõi thêm thể loại mới

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
    private List<ComicGenre> comicGenres; // Danh sách truyện thuộc thể loại này, hỗ trợ hiển thị và lọc
}
