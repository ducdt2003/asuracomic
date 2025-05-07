package com.example.asuracomic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comic_authors", indexes = @Index(name = "idx_comic_id_author_id", columnList = "comic_id, author_id", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComicAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của bản ghi, dùng để tham chiếu

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic; // Truyện liên kết, xác định tác giả của truyện

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author; // Tác giả liên kết, hiển thị trên trang chi tiết truyện
}
