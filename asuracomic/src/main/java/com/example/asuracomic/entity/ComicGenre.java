package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comic_genres", indexes = @Index(name = "idx_comic_id_genre_id", columnList = "comic_id, genre_id", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComicGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của bản ghi, dùng để tham chiếu

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic; // Truyện liên kết, xác định truyện thuộc thể loại nào

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre; // Thể loại liên kết, hiển thị thẻ thể loại trên trang chi tiết truyện
}