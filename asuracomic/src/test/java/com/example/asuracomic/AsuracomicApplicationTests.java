package com.example.asuracomic;

import com.example.asuracomic.entity.*;
import com.example.asuracomic.model.enums.*;
import com.example.asuracomic.repository.*;
import com.github.javafaker.Faker;
import com.github.slugify.Slugify;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest // Khởi tạo môi trường kiểm thử Spring Boot
class AsuraComicApplicationTests {

	@Autowired // Tiêm dependency cho repository của User
	private UserRepository userRepository;

	@Autowired // Tiêm dependency cho repository của Comic
	private ComicRepository comicRepository;

	@Autowired // Tiêm dependency cho repository của Author
	private AuthorRepository authorRepository;

	@Autowired // Tiêm dependency cho repository của ComicAuthor
	private ComicAuthorRepository comicAuthorRepository;

	@Autowired // Tiêm dependency cho repository của Artist
	private ArtistRepository artistRepository;

	@Autowired // Tiêm dependency cho repository của ComicArtist
	private ComicArtistRepository comicArtistRepository;

	@Autowired // Tiêm dependency cho repository của Genre
	private GenreRepository genreRepository;

	@Autowired // Tiêm dependency cho repository của ComicGenre
	private ComicGenreRepository comicGenreRepository;

	@Autowired // Tiêm dependency cho repository của Chapter
	private ChapterRepository chapterRepository;

	@Autowired // Tiêm dependency cho repository của ChapterImage
	private ChapterImageRepository chapterImageRepository;

	@Autowired // Tiêm dependency cho repository của ComicView
	private ComicViewRepository comicViewRepository;

	@Autowired // Tiêm dependency cho repository của Favorite
	private FavoriteRepository favoriteRepository;

	@Autowired // Tiêm dependency cho repository của Rating
	private RatingRepository ratingRepository;

	@Autowired // Tiêm dependency cho repository của Comment
	private CommentRepository commentRepository;

	@Autowired // Tiêm dependency cho repository của VipConfig
	private VipConfigRepository vipConfigRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private final Faker faker = new Faker(); // Khởi tạo đối tượng Faker để tạo dữ liệu giả
	private final Slugify slugify = Slugify.builder().build(); // Khởi tạo đối tượng Slugify để tạo chuỗi thân thiện với URL
	private final Random random = new Random(); // Khởi tạo đối tượng Random để tạo số ngẫu nhiên

	@Test
	void save_users() {
		// Tạo 20 người dùng ngẫu nhiên
		for (int i = 0; i < 20; i++) {
			String displayName = faker.name().fullName(); // Tạo tên hiển thị ngẫu nhiên
			User user = User.builder() // Sử dụng builder pattern để tạo đối tượng User
					.email(faker.internet().emailAddress()) // Tạo email ngẫu nhiên
					.username(faker.name().username()) // Tạo tên người dùng ngẫu nhiên
					.avatar("https://placehold.co/600x400?text=" + displayName.substring(0, 1).toUpperCase()) // Tạo URL avatar
					.password("123") // Đặt mật khẩu mặc định
					.role(i < 1 ? Role.ADMIN : Role.USER) // Gán vai trò
					.coinBalance(BigDecimal.valueOf(faker.number().numberBetween(0, 1000))) // Tạo số dư coin
					.vipStatus(false) // Đặt trạng thái VIP
					.isActive(true) // Đặt trạng thái hoạt động
					.createdAt(LocalDateTime.now()) // Gán thời điểm tạo
					.updatedAt(LocalDateTime.now()) // Gán thời điểm cập nhật
					.lastLogin(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30))) // Gán thời điểm đăng nhập cuối ngẫu nhiên trong 30 ngày qua
					.build();
			userRepository.save(user); // Lưu người dùng vào cơ sở dữ liệu
		}
	}

	@Test
	void update_user_password() {
		List<User> users = userRepository.findAll();
		for (User user : users) {
			String password = user.getPassword();
			String newPassword = passwordEncoder.encode(password);
			user.setPassword(newPassword);
			userRepository.save(user);
		}
	}

	@Test
	void save_authors() {
		// Tạo 20 tác giả ngẫu nhiên
		for (int i = 0; i < 30; i++) {
			String name = faker.book().author(); // Tạo tên tác giả ngẫu nhiên
			Author author = Author.builder() // Sử dụng builder pattern để tạo đối tượng Author
					.name(name) // Gán tên tác giả
					.slug(slugify.slugify(name)) // Tạo slug từ tên tác giả
					.createdAt(LocalDateTime.now()) // Gán thời điểm tạo là hiện tại
					.build();
			authorRepository.save(author); // Lưu tác giả vào cơ sở dữ liệu
		}
	}

	@Test
	void save_artists() {
		// Tạo 20 họa sĩ ngẫu nhiên
		for (int i = 0; i < 20; i++) {
			String name = faker.name().fullName(); // Tạo tên họa sĩ ngẫu nhiên
			Artist artist = Artist.builder() // Sử dụng builder pattern để tạo đối tượng Artist
					.name(name) // Gán tên họa sĩ
					.slug(slugify.slugify(name)) // Tạo slug từ tên họa sĩ
					.createdAt(LocalDateTime.now()) // Gán thời điểm tạo là hiện tại
					.build();
			artistRepository.save(artist); // Lưu họa sĩ vào cơ sở dữ liệu
		}
	}

	@Test
	void save_genres() {
		Set<String> addedSlugs = new HashSet<>();
		int count = 0;

		while (count < 10) {
			String name = faker.book().genre();
			String slug = slugify.slugify(name);

			// Kiểm tra slug đã tồn tại trong DB hoặc trong vòng lặp
			if (addedSlugs.contains(slug) || genreRepository.findBySlug(slug) != null) {
				continue;
			}

			addedSlugs.add(slug);

			Genre genre = Genre.builder()
					.name(name)
					.slug(slug)
					.createdAt(LocalDateTime.now())
					.build();

			genreRepository.save(genre);
			count++;
		}
	}

	@Test
	void save_comics() {
		List<Author> authors = authorRepository.findAll();
		List<Artist> artists = artistRepository.findAll();
		List<Genre> genres = genreRepository.findAll();

		int createdCount = 0;
		int targetCount = 30; // Thay đổi từ 300 thành 50
		Set<String> usedSlugs = new HashSet<>(comicRepository.findAll().stream()
				.map(Comic::getSlug)
				.toList());

		while (createdCount < targetCount) {
			Author rdAuthor = authors.get(random.nextInt(authors.size()));
			Artist rdArtist = artists.get(random.nextInt(artists.size()));
			List<Genre> rdGenres = new ArrayList<>();

			while (rdGenres.size() < 3) { // 3 unique genres
				Genre rdGenre = genres.get(random.nextInt(genres.size()));
				if (!rdGenres.contains(rdGenre)) {
					rdGenres.add(rdGenre);
				}
			}

			String baseTitle = faker.book().title();
			String slug = slugify.slugify(baseTitle);
			int suffix = 1;

			// Tránh trùng slug
			while (usedSlugs.contains(slug)) {
				slug = slugify.slugify(baseTitle + "-" + suffix++);
			}
			usedSlugs.add(slug);

			String thumbnail = "https://placehold.co/600x400?text=" + baseTitle.charAt(0);
			boolean status = faker.bool().bool();

			Comic comic = Comic.builder()
					.title(baseTitle)
					.slug(slug)
					.description(faker.lorem().paragraph())
					.coverImage(thumbnail)
					.viewCount((long) faker.number().numberBetween(1_000, 100_000))
					.followCount((long) faker.number().numberBetween(100, 50_000))
					.averageRating(BigDecimal.valueOf(faker.number().randomDouble(1, 1, 5)))
					.status(status ? ComicStatus.ONGOING : ComicStatus.COMPLETED)
					.type(random.nextInt(ComicType.values().length) % 2 == 0 ? ComicType.MANHWA : ComicType.MANGA)
					.isPublished(status)
					.serialization(faker.company().name())
					.createdAt(LocalDateTime.now())
					.updatedAt(LocalDateTime.now())
					.build();
			comicRepository.save(comic);

			comicAuthorRepository.save(ComicAuthor.builder().comic(comic).author(rdAuthor).build());
			comicArtistRepository.save(ComicArtist.builder().comic(comic).artist(rdArtist).build());

			for (Genre genre : rdGenres) {
				comicGenreRepository.save(ComicGenre.builder().comic(comic).genre(genre).build());
			}

			createdCount++;
		}

		System.out.println("✅ Đã tạo " + createdCount + " truyện thành công.");
	}


	@Test
	void save_chapters() {
		// Lấy danh sách tất cả truyện từ cơ sở dữ liệu
		List<Comic> comics = comicRepository.findAll();
		for (Comic comic : comics) {
			int chapterCount = faker.number().numberBetween(5, 10); // Tạo số lượng chương ngẫu nhiên từ 5 đến 10
			for (int i = 1; i <= chapterCount; i++) {
				Chapter chapter = Chapter.builder() // Sử dụng builder pattern để tạo đối tượng Chapter
						.comic(comic) // Gán truyện
						.title("Chapter " + i) // Gán tiêu đề chương
						.slug(slugify.slugify("chapter-" + i)) // Tạo slug từ tiêu đề
						.chapterNumber(i) // Gán số thứ tự chương
						.isFree(i <= 2) // Chương 1 và 2 là miễn phí
						.coinPrice(i <= 2 ? BigDecimal.ZERO : BigDecimal.valueOf(faker.number().numberBetween(5, 20))) // Giá coin, miễn phí nếu i <= 2, còn lại ngẫu nhiên từ 5 đến 20
						.viewCount((long) faker.number().numberBetween(100, 10000)) // Tạo số lượt xem từ 100 đến 10000
						.isPublished(true) // Gán trạng thái xuất bản là true
						.createdAt(LocalDateTime.now()) // Gán thời điểm tạo là hiện tại
						.updatedAt(LocalDateTime.now()) // Gán thời điểm cập nhật là hiện tại
						.build();
				chapterRepository.save(chapter); // Lưu chương vào cơ sở dữ liệu
			}
		}
	}

	@Test
	void save_chapter_images() {
		// Lấy danh sách tất cả chương từ cơ sở dữ liệu
		List<Chapter> chapters = chapterRepository.findAll();
		for (Chapter chapter : chapters) {
			int imageCount = faker.number().numberBetween(10, 20); // Tạo số lượng ảnh ngẫu nhiên từ 10 đến 20
			for (int i = 1; i <= imageCount; i++) {
				ChapterImage chapterImage = ChapterImage.builder() // Sử dụng builder pattern để tạo đối tượng ChapterImage
						.chapter(chapter) // Gán chương
						.imageUrl("https://placehold.co/800x1200?text=Page-" + i) // Tạo URL ảnh ngẫu nhiên
						.orderIndex(i) // Gán thứ tự ảnh
						.createdAt(LocalDateTime.now()) // Gán thời điểm tạo là hiện tại
						.build();
				chapterImageRepository.save(chapterImage); // Lưu ảnh vào cơ sở dữ liệu
			}
		}
	}

	@Test
	void save_comic_views() {
		// Lấy danh sách tất cả truyện từ cơ sở dữ liệu
		List<Comic> comics = comicRepository.findAll();
		for (Comic comic : comics) {
			// Tạo số lượt xem ngẫu nhiên từ 1 triệu đến 5 triệu cho mỗi truyện
			int viewCount = faker.number().numberBetween(1_000_000, 5_000_000);
			for (int i = 0; i < viewCount / 1000; i++) { // Chia nhỏ để giảm số bản ghi tạo ra
				ComicView comicView = ComicView.builder() // Sử dụng builder pattern để tạo đối tượng ComicView
						.comic(comic) // Gán truyện
						.viewedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30))) // Gán thời điểm xem ngẫu nhiên trong 30 ngày qua
						.build();
				comicViewRepository.save(comicView); // Lưu lượt xem vào cơ sở dữ liệu
			}
			// Cập nhật số lượt xem trong bảng Comic
			comic.setViewCount((long) viewCount);
			comicRepository.save(comic); // Lưu lại truyện với số lượt xem mới
		}
	}

	// CHƯA test
	@Test
	void save_favorites() {
		// Lấy danh sách tất cả truyện (Comic) từ cơ sở dữ liệu
		List<Comic> comics = comicRepository.findAll();
		// Lấy danh sách tất cả người dùng (User) từ cơ sở dữ liệu
		List<User> users = userRepository.findAll();

		// Duyệt qua từng người dùng
		for (User user : users) {
			// Với mỗi người dùng, tạo ngẫu nhiên từ 20 đến 30 truyện yêu thích (random.nextInt(11) trả về 0-10, cộng thêm 20)
			for (int i = 0; i < random.nextInt(11) + 20; i++) {
				// Chọn ngẫu nhiên một truyện từ danh sách comics
				Comic comic = comics.get(random.nextInt(comics.size()));

				// Kiểm tra xem người dùng đã thêm truyện này vào danh sách yêu thích chưa
				// Phương thức existsByUser_IdAndComic_Id kiểm tra cặp userId và comicId có tồn tại trong bảng favorites không
				boolean isExist = favoriteRepository.existsByUser_IdAndComic_Id(user.getId(), comic.getId());

				// Nếu chưa tồn tại (isExist = false), tạo mới một bản ghi Favorite
				if (!isExist) {
					// Sử dụng builder pattern để tạo đối tượng Favorite
					Favorite favorite = Favorite.builder()
							.user(user) // Gán người dùng hiện tại
							.comic(comic) // Gán truyện được chọn
							.createdAt(LocalDateTime.now()) // Gán thời điểm tạo là thời gian hiện tại
							.build();

					// Lưu bản ghi Favorite vào cơ sở dữ liệu
					favoriteRepository.save(favorite);
				}
				// Nếu đã tồn tại, bỏ qua để tránh trùng lặp
			}
		}
	}

	@Test
	void save_ratings() {
		// Lấy danh sách tất cả người dùng (User) từ cơ sở dữ liệu
		List<User> users = userRepository.findAll();
		// Lấy danh sách tất cả truyện (Comic) từ cơ sở dữ liệu
		List<Comic> comics = comicRepository.findAll();

		// Duyệt qua từng truyện
		for (Comic comic : comics) {
			// Với mỗi truyện, tạo ngẫu nhiên từ 10 đến 20 đánh giá (random.nextInt(11) trả về 0-10, cộng thêm 10)
			for (int i = 0; i < random.nextInt(11) + 10; i++) {
				// Chọn ngẫu nhiên một người dùng từ danh sách users
				User rdUser = users.get(random.nextInt(users.size()));

				// Kiểm tra xem người dùng đã đánh giá truyện này chưa
				// Phương thức existsByUser_IdAndComic_Id kiểm tra cặp userId và comicId có tồn tại trong bảng ratings không
				boolean isExist = ratingRepository.existsByUser_IdAndComic_Id(rdUser.getId(), comic.getId());

				// Nếu chưa tồn tại (isExist = false), tạo mới một bản ghi Rating
				if (!isExist) {
					// Sử dụng builder pattern để tạo đối tượng Rating
					Rating rating = Rating.builder()
							.user(rdUser) // Gán người dùng hiện tại
							.comic(comic) // Gán truyện được chọn
							.score(random.nextInt(5) + 1) // Điểm đánh giá ngẫu nhiên từ 1 đến 5
							.createdAt(LocalDateTime.now()) // Gán thời điểm tạo là thời gian hiện tại
							.build();

					// Lưu bản ghi Rating vào cơ sở dữ liệu
					ratingRepository.save(rating);
				}
				// Nếu đã tồn tại, bỏ qua để tránh trùng lặp
			}
		}
	}

	@Test
	void save_comments() {
		// Lấy danh sách tất cả người dùng từ cơ sở dữ liệu
		List<User> users = userRepository.findAll();
		// Lấy danh sách tất cả chương từ cơ sở dữ liệu
		List<Chapter> chapters = chapterRepository.findAll();
		for (Chapter chapter : chapters) {
			// Với mỗi chương, tạo ngẫu nhiên từ 5 đến 15 bình luận (random.nextInt(11) trả về 0-10, cộng thêm 5)
			for (int i = 0; i < random.nextInt(11) + 5; i++) {
				User rdUser = users.get(random.nextInt(users.size())); // Chọn ngẫu nhiên một người dùng
				Comment comment = Comment.builder() // Sử dụng builder pattern để tạo đối tượng Comment
						.user(rdUser) // Gán người dùng
						.chapter(chapter) // Gán chương
						.content(faker.lorem().paragraph()) // Tạo nội dung bình luận ngẫu nhiên
						.status(CommentStatus.ACTIVE) // Gán trạng thái là ACTIVE
						.isEdited(false) // Gán trạng thái chỉnh sửa là false
						.createdAt(LocalDateTime.now()) // Gán thời điểm tạo là hiện tại
						.updatedAt(LocalDateTime.now()) // Gán thời điểm cập nhật là hiện tại
						.build();
				commentRepository.save(comment); // Lưu bình luận vào cơ sở dữ liệu
			}
		}
	}

	@Test
	void save_vip_configs() {
		String[] vipNames = {"Monthly VIP", "Quarterly VIP", "Yearly VIP"};
		int[] durations = {30, 90, 365};
		for (int i = 0; i < vipNames.length; i++) {
			try {
				String slug = slugify.slugify(vipNames[i]);
				System.out.println("Generated slug: " + slug);
				VipConfig vipConfig = VipConfig.builder()
						.name(vipNames[i])
						.slug(slug)
						.coinPrice(BigDecimal.valueOf(faker.number().numberBetween(10, 999)).setScale(2, BigDecimal.ROUND_HALF_UP))
						.durationDays(durations[i])
						.isActive(true)
						.createdAt(LocalDateTime.now())
						.updatedAt(LocalDateTime.now())
						.build();
				vipConfigRepository.save(vipConfig);
				System.out.println("Saved VIP config: " + vipNames[i]);
			} catch (Exception e) {
				System.err.println("Error saving VIP config: " + vipNames[i]);
				e.printStackTrace();
			}
		}
	}
}