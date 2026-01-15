package com.example.asuracomic.service.admin.admincomic;

import com.example.asuracomic.dto.admin.ComicUpdateForm;
import com.example.asuracomic.entity.*;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import com.example.asuracomic.repository.ArtistRepository;
import com.example.asuracomic.repository.AuthorRepository;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminComicService {
    private final ComicRepository comicRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final ArtistRepository artistRepository;

    @Transactional
    public void updateComic(String slug, ComicUpdateForm form) {
        Comic comic = comicRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện với slug: " + slug));

        // 1. Cập nhật thông tin cơ bản
        comic.setTitle(form.getTitle());
        comic.setSlug(form.getSlug());
        comic.setDescription(form.getDescription());
        comic.setSerialization(form.getSerialization());
        comic.setStatus(ComicStatus.valueOf(form.getStatus()));
        comic.setType(ComicType.valueOf(form.getType()));
        comic.setPublished(form.isPublished());
        comic.setCoverImage(form.getCoverImage());
        comic.setUpdatedAt(LocalDateTime.now());

        // 2. Xử lý xóa cũ và lưu trạng thái trống để ép Hibernate chạy lệnh DELETE
        comic.getComicGenres().clear();
        comic.getComicAuthors().clear();
        comic.getComicArtists().clear();

        // Ép Hibernate thực hiện lệnh DELETE tất cả các liên kết cũ ngay bây giờ
        comicRepository.saveAndFlush(comic);

        // 3. Sau khi đã sạch dữ liệu cũ trong DB, bắt đầu thêm mới
        if (form.getGenreIds() != null) {
            form.getGenreIds().forEach(id -> {
                Genre genre = genreRepository.findById(id).orElseThrow();
                comic.getComicGenres().add(ComicGenre.builder().comic(comic).genre(genre).build());
            });
        }

        if (form.getAuthorIds() != null) {
            form.getAuthorIds().forEach(id -> {
                Author author = authorRepository.findById(id).orElseThrow();
                comic.getComicAuthors().add(ComicAuthor.builder().comic(comic).author(author).build());
            });
        }

        if (form.getArtistIds() != null) {
            form.getArtistIds().forEach(id -> {
                Artist artist = artistRepository.findById(id).orElseThrow();
                comic.getComicArtists().add(ComicArtist.builder().comic(comic).artist(artist).build());
            });
        }

        // 4. Lưu lại toàn bộ dữ liệu mới
        comicRepository.save(comic);
    }



}
