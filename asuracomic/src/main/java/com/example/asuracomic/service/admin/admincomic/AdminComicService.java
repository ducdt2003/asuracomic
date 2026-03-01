package com.example.asuracomic.service.admin.admincomic;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.asuracomic.dto.admin.ComicCreateRequest;
import com.example.asuracomic.dto.admin.ComicUpdateForm;
import com.example.asuracomic.entity.*;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import com.example.asuracomic.repository.*;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminComicService {
    private final ComicRepository comicRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final ArtistRepository artistRepository;
    private final ChapterImageRepository chapterImageRepository;
    private final ChapterRepository chapterRepository;
    private final ComicArtistRepository comicArtistRepository;
    private final ComicAuthorRepository comicAuthorRepository;
    private final ComicGenreRepository comicGenreRepository;
    private final CommentRepository commentRepository;
    private final UnlockedChapterRepository unlockedChapterRepository;
    private final FavoriteRepository favoriteRepository;
    private final RatingRepository ratingRepository;
    private final ComicViewRepository comicViewRepository;
    private final TransactionRepository transactionRepository;
    private final Cloudinary cloudinary;

    @Transactional
    public void updateComic(String slug, ComicUpdateForm form) {
        Comic comic = comicRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện với slug: " + slug));

        // 1. Cập nhật thông tin cơ bản (Trừ coverImage để xử lý riêng)
        comic.setTitle(form.getTitle());
        comic.setSlug(form.getSlug());
        comic.setDescription(form.getDescription());
        comic.setSerialization(form.getSerialization());
        comic.setStatus(ComicStatus.valueOf(form.getStatus()));
        comic.setType(ComicType.valueOf(form.getType()));
        comic.setPublished(form.isPublished());
        comic.setUpdatedAt(LocalDateTime.now());

        // 2. Xử lý ảnh bìa
        if (form.getCoverImageFile() != null && !form.getCoverImageFile().isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(form.getCoverImageFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                String url = (String) uploadResult.get("secure_url");
                comic.setCoverImage(url); // Gán URL mới từ Cloudinary
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi tải ảnh lên Cloudinary: " + e.getMessage());
            }
        } else {
            // Nếu không chọn file mới, chỉ gán lại URL từ form nếu nó khác null (giữ ảnh cũ)
            if (form.getCoverImage() != null) {
                comic.setCoverImage(form.getCoverImage());
            }
        }

        // 3. Xử lý xóa cũ và lưu trạng thái trống để ép Hibernate chạy lệnh DELETE
        comic.getComicGenres().clear();
        comic.getComicAuthors().clear();
        comic.getComicArtists().clear();

        // Ép Hibernate thực hiện lệnh DELETE tất cả các liên kết cũ ngay bây giờ
        comicRepository.saveAndFlush(comic);

        // 4. Bắt đầu thêm mới các liên kết (Giữ nguyên logic của ông)
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

        // 5. Lưu lại toàn bộ dữ liệu cuối cùng
        comicRepository.save(comic);
    }
    public static ComicUpdateForm fromComic(Comic comic) {
        return ComicUpdateForm.builder()
                .title(comic.getTitle())
                .slug(comic.getSlug())
                .coverImage(comic.getCoverImage())
                .description(comic.getDescription())
                .serialization(comic.getSerialization())
                .status(comic.getStatus().name()) // Chuyển Enum thành String
                .type(comic.getType().name())     // Chuyển Enum thành String
                .isPublished(comic.isPublished())
                .genreIds(comic.getComicGenres().stream()
                        .map(cg -> cg.getGenre().getId()).collect(Collectors.toList()))
                .authorIds(comic.getComicAuthors().stream()
                        .map(ca -> ca.getAuthor().getId()).collect(Collectors.toList()))
                .artistIds(comic.getComicArtists().stream()
                        .map(ca -> ca.getArtist().getId()).collect(Collectors.toList()))
                .build();
    }


    @Transactional
    public void deleteComic(Long comicId) {
        if (!comicRepository.existsById(comicId)) {
            throw new RuntimeException("Comic không tồn tại");
        }

        // 1. Lấy tất cả Chapter ID của truyện này
        List<Long> chapterIds = chapterRepository.findIdsByComicId(comicId);

        // 2. Xử lý các bảng phụ theo từng Chapter
        for (Long chapterId : chapterIds) {
            // ⭐ BƯỚC QUAN TRỌNG: Gỡ bỏ liên kết trong bảng Transaction
            // Thay vì xóa giao dịch (để giữ lại lịch sử tài chính), ta đặt chapter_id = null
            transactionRepository.setChapterNullByChapterId(chapterId);

            chapterImageRepository.deleteByChapterId(chapterId);
            commentRepository.deleteByChapterId(chapterId);
            unlockedChapterRepository.deleteByChapterId(chapterId);
        }

        // 3. Xóa các bảng liên quan trực tiếp đến Comic
        comicGenreRepository.deleteByComicId(comicId);
        comicAuthorRepository.deleteByComicId(comicId);
        comicArtistRepository.deleteByComicId(comicId);
        favoriteRepository.deleteByComicId(comicId);
        ratingRepository.deleteByComicId(comicId);
        comicViewRepository.deleteByComicId(comicId);

        // 4. Bây giờ mới xóa Chapter (vì Transaction đã không còn trỏ vào nữa)
        chapterRepository.deleteByComicId(comicId);

        // 5. Cuối cùng xóa Comic
        comicRepository.deleteById(comicId);
        comicRepository.flush();
    }

    private final Slugify slugify = Slugify.builder().build();
    @Transactional
    public Comic createComic(ComicCreateRequest request) {
        // 1. Tạo Slug và kiểm tra trùng lặp
        String baseSlug = slugify.slugify(request.getTitle());
        String finalSlug = baseSlug;
        int suffix = 1;
        while (comicRepository.existsBySlug(finalSlug)) {
            finalSlug = baseSlug + "-" + suffix++;
        }

        // 2. Tạo đối tượng Comic
        Comic comic = Comic.builder()
                .title(request.getTitle())
                .slug(finalSlug)
                .description(request.getDescription())
                .coverImage(request.getCoverImage())
                .viewCount(0L)
                .followCount(0L)
                .averageRating(BigDecimal.ZERO)
                .status(request.getStatus())
                .type(request.getType())
                .serialization(request.getSerialization())
                .isPublished(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Comic savedComic = comicRepository.save(comic);

        // 3. Lưu quan hệ Tác giả (Xử lý Text -> Tìm hoặc Tạo mới)
        if (request.getAuthorIds() != null) { // Lưu ý: Bạn có thể đổi tên field trong DTO thành authorNames cho rõ nghĩa
            request.getAuthorIds().forEach(name -> {
                Author author = authorRepository.findByName(name)
                        .orElseGet(() -> {
                            // Nếu không thấy tên này, tạo mới Author
                            String authorSlug = slugify.slugify(name);
                            return authorRepository.save(Author.builder()
                                    .name(name)
                                    .slug(authorSlug)
                                    .createdAt(LocalDateTime.now())
                                    .build());
                        });
                comicAuthorRepository.save(ComicAuthor.builder().comic(savedComic).author(author).build());
            });
        }

        // 4. Lưu quan hệ Họa sĩ (Xử lý Text -> Tìm hoặc Tạo mới)
        if (request.getArtistIds() != null) {
            request.getArtistIds().forEach(name -> {
                Artist artist = artistRepository.findByName(name)
                        .orElseGet(() -> {
                            String artistSlug = slugify.slugify(name);
                            return artistRepository.save(Artist.builder()
                                    .name(name)
                                    .slug(artistSlug)
                                    .createdAt(LocalDateTime.now())
                                    .build());
                        });
                comicArtistRepository.save(ComicArtist.builder().comic(savedComic).artist(artist).build());
            });
        }

        // 5. Lưu quan hệ Thể loại (Xử lý Text -> Tìm hoặc Tạo mới)
        if (request.getGenreIds() != null) {
            request.getGenreIds().forEach(name -> {
                Genre genre = genreRepository.findByName(name)
                        .orElseGet(() -> {
                            String genreSlug = slugify.slugify(name);
                            return genreRepository.save(Genre.builder()
                                    .name(name)
                                    .slug(genreSlug)
                                    .createdAt(LocalDateTime.now())
                                    .build());
                        });
                comicGenreRepository.save(ComicGenre.builder().comic(savedComic).genre(genre).build());
            });
        }
        return savedComic;
    }



   /* top danh sách truyện có đánh giá cao nhất */
    public Page<Comic> getTopRatedComics(int page) {
        // page: số trang (bắt đầu từ 0)
        // size: 5 (số lượng phần tử trên 1 trang)
        Pageable pageable = PageRequest.of(page, 5);
        return comicRepository.findAllByOrderByAverageRatingDesc(pageable);
    }

    public Page<Comic> getTopViewedComics(int page) {
        Pageable pageable = PageRequest.of(page, 5);
        return comicRepository.findAllByOrderByViewCountDesc(pageable);
    }



}
