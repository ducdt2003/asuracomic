package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comic_artists", indexes = @Index(name = "idx_comic_id_artist_id", columnList = "comic_id, artist_id", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComicArtist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của bản ghi, dùng để tham chiếu

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic; // Truyện liên kết, xác định họa sĩ của truyện

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist; // Họa sĩ liên kết, hiển thị trên trang chi tiết truyện
}
