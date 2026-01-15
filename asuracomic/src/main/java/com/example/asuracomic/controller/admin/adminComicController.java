package com.example.asuracomic.controller.admin;

import com.example.asuracomic.dto.admin.*;
import com.example.asuracomic.entity.*;
import com.example.asuracomic.model.enums.Role;
import com.example.asuracomic.repository.*;
import com.example.asuracomic.service.DashboardService;
import com.example.asuracomic.service.admin.admincomic.AdminChapterService;
import com.example.asuracomic.service.admin.admincomic.AdminComicService;
import com.example.asuracomic.service.admin.adminuser.AdminUser;
import com.example.asuracomic.service.admin.RevenueService;
import com.example.asuracomic.service.admin.adminuser.UserDelete;
import com.example.asuracomic.service.admin.adminuser.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
// Trong file RevenueController.java


@Controller
@RequestMapping("/asura/admin/comic")
@RequiredArgsConstructor
public class adminComicController {
    private final DashboardService dashboardService;
    private final CommentRepository commentRepository;
    private final RevenueService revenueService;
    private final TransactionRepository transactionRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ComicRepository comicRepository;
    private final AdminUser adminUser;
    private final UserService createUser;
    private final UserDelete userDelete;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final ArtistRepository artistRepository;
    private final AdminComicService adminComicService;
    private final AdminChapterService adminChapterService;
    private final ChapterImageRepository chapterImageRepository;

    /* <!-- ================= Comic ================= -->*/
    @GetMapping
    public String getIndexPage(Model model) {
        // Dashboard statistics
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        model.addAttribute("stats", stats);
        return "admin-templ/index";
    }

    @GetMapping("/content/detail")
    public String getDetailPage(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 5;
        Page<Comic> comicPage = dashboardService.getComicPage(PageRequest.of(page, pageSize));
        model.addAttribute("comics", comicPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", comicPage.getTotalPages());
        return "admin-templ/admin-content/blog-index";
    }

    // update truyện vtair truyên lên
    @GetMapping("/content/create")
    public String getCreatePage() {
        return "admin-templ/admin-content/blog-create";
    }

    // chi tiết truyện
    @GetMapping("/content/{slug}")
    public String getComicDetail(@PathVariable String slug,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        // Lấy thông tin truyện
        Comic comic = dashboardService.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));

        // Lấy danh sách chương có phân trang
        Page<Chapter> chapterPage = dashboardService.getChaptersByComic(comic, page, size);

        model.addAttribute("comic", comic);
        model.addAttribute("chapterPage", chapterPage);
        return "admin-templ/admin-content/blog-detail"; // file HTML
    }


    // chỉnh sữa truyện
    @GetMapping("/content/edit/{slug}")
    public String getEditPage(@PathVariable String slug,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              Model model) {
        Comic comic = dashboardService.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));
        // Lấy toàn bộ danh sách để đổ vào các ô Select
        model.addAttribute("allGenres", genreRepository.findAll());
        model.addAttribute("allAuthors", authorRepository.findAll());
        model.addAttribute("allArtists", artistRepository.findAll());

        // Lấy danh sách ID đã chọn để Thymeleaf check "selected"
        model.addAttribute("selectedGenreIds", comic.getComicGenres().stream().map(cg -> cg.getGenre().getId()).toList());
        model.addAttribute("selectedAuthorIds", comic.getComicAuthors().stream().map(ca -> ca.getAuthor().getId()).toList());
        model.addAttribute("selectedArtistIds", comic.getComicArtists().stream().map(ca -> ca.getArtist().getId()).toList());
        // Lấy danh sách chương có phân trang
        Page<Chapter> chapterPage = dashboardService.getChaptersByComic(comic, page, size);
        model.addAttribute("comic", comic);
        model.addAttribute("chapterPage", chapterPage);
        return "admin-templ/admin-content/blog-edit";
    }
    @PostMapping("/content/edit/{slug}")
    public String handleUpdateComic(@PathVariable String slug,
                                    @ModelAttribute("comicForm") ComicUpdateForm form,
                                    RedirectAttributes redirectAttributes) {
        try {
            adminComicService.updateComic(slug, form);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin truyện thành công!");

            // Điều hướng theo slug mới để tránh lỗi 404
            return "redirect:/asura/admin/comic/content/edit/" + form.getSlug();
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để debug
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/asura/admin/comic/content/edit/" + slug;
        }
    }



    // Trang cập nhật nội dung chương
    @GetMapping("/content/chapter/update/{id}")
    public String getChapterUpdatePage(@PathVariable("id") Long comicId,
                                       Model model) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));

        model.addAttribute("comic", comic);
        return "admin-templ/admin-content/chapter-update";
    }


    @PostMapping("/content/chapter/update/{comicId}")
    public String handleCreateChapter(
            @PathVariable("comicId") Long comicId,
            @ModelAttribute ChapterUpdateForm chapterRequest,
            RedirectAttributes redirectAttributes) {

        try {
            adminChapterService.createChapter(comicId, chapterRequest);
            redirectAttributes.addFlashAttribute("success", "Tải lên chương mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/asura/admin/comic/content/chapter/update/" + comicId;
    }






    // Trang chỉnh sửa metadata / thông tin chương
    @GetMapping("/content/chapter/edit/{id}")
    public String getChapterEditPage(@PathVariable Long id, Model model) {
        Chapter chapter = adminChapterService.getChapterById(id);
        model.addAttribute("chapter", chapter);
        // Sắp xếp ảnh theo thứ tự trang trước khi gửi ra View
        chapter.getChapterImages().sort((a, b) -> a.getOrderIndex().compareTo(b.getOrderIndex()));
        return "admin-templ/admin-content/chapters-edit";
    }

    @PostMapping("/content/chapter/edit/{id}")
    public String handleUpdateChapter(@PathVariable Long id,
                                      @ModelAttribute ChapterEditFormDTO form,
                                      RedirectAttributes redirectAttributes) {
        try {
            adminChapterService.updateChapter(id, form);
            redirectAttributes.addFlashAttribute("success", "Cập nhật chương thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/asura/admin/comic/content/chapter/edit/" + id;
    }

    @PostMapping("/content/chapter/edit/delete-image/{imageId}")
    public String deleteImage(@PathVariable Long imageId,
                              RedirectAttributes redirectAttributes) {
        try {
            Long chapterId = adminChapterService.deleteImageAndReorder(imageId);

            redirectAttributes.addFlashAttribute(
                    "success", "Đã xóa trang truyện thành công!");

            return "redirect:/asura/admin/comic/content/chapter/edit/" + chapterId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Lỗi: " + e.getMessage());
            return "redirect:/asura/admin/comic";
        }
    }

    @PostMapping("/content/chapter/edit/add-images/{chapterId}")
    public String addChapterImages(
            @PathVariable Long chapterId,
            @RequestParam("images") List<MultipartFile> images,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminChapterService.addImagesToChapter(chapterId, images);

            redirectAttributes.addFlashAttribute(
                    "success", "Đã thêm ảnh vào chương thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Lỗi: " + e.getMessage());
        }

        // Quay lại đúng trang chỉnh sửa chapter
        return "redirect:/asura/admin/comic/content/chapter/edit/" + chapterId;
    }








    /* <!-- ================= USER ================= -->*/
    /*@GetMapping("/users")
    public String getUserPage(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) Boolean vipStatus,
                              @RequestParam(required = false) Role role,
                              Model model) {
        int pageSize = 10; // Số người dùng mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<User> usersPage = dashboardService.getUserPage(pageable, search, vipStatus, role);

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("vipStatus", vipStatus);
        model.addAttribute("role", role);
        return "admin-templ/user";
    }*/
    @GetMapping("/users")
    public String getUserPage(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) Boolean vipStatus,
                              @RequestParam(required = false) Role role,
                              @RequestParam(defaultValue = "id") String sortBy,
                              @RequestParam(defaultValue = "asc") String sortDir,
                              Model model) {
        int pageSize = 10;
        page = Math.max(0, page);
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Order.asc(sortBy) : Sort.Order.desc(sortBy));
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        dashboardService.updateVipStatus(); // Cập nhật trạng thái VIP
        Page<User> usersPage = dashboardService.getUserPage(pageable, search, vipStatus, role);
        if (page > usersPage.getTotalPages() - 1 && usersPage.getTotalPages() > 0) {
            page = usersPage.getTotalPages() - 1;
            pageable = PageRequest.of(page, pageSize, sort);
            usersPage = dashboardService.getUserPage(pageable, search, vipStatus, role);
        }
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("vipStatus", vipStatus);
        model.addAttribute("role", role);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "admin-templ/admin-user/user";
    }


    @GetMapping("/users/edit/{id}")
    public String getUserEditPage(@PathVariable Long id, Model model) {
        // 1. Tìm user, nếu không thấy quăng lỗi hoặc chuyển hướng
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        // 2. Đưa đối tượng user vào model
        model.addAttribute("user", user);

        // 3. Đưa danh sách Roles vào để hiển thị trong dropdown
        model.addAttribute("roles", Role.values());
        return "admin-templ/admin-user/edit-user";
    }
    // Xử lý cập nhật thông tin sữa
  /*  @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") UserUpdateDto userDto,
                             RedirectAttributes redirectAttributes) {
        try {
            adminUser.updateUser(id, userDto);
            redirectAttributes.addFlashAttribute(
                    "successMsg", "Dữ liệu đã được cập nhật vào Database!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMsg", "Lỗi lưu dữ liệu: " + e.getMessage());
        }

        return "redirect:/asura/admin/comic/users/edit/" + id;
    }*/
    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") UserUpdateDto userDto,
                             @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile, // Thêm dòng này
                             RedirectAttributes redirectAttributes) {
        try {
            // Truyền thêm file ảnh vào service
            adminUser.updateUser(id, userDto, avatarFile);
            redirectAttributes.addFlashAttribute(
                    "successMsg", "Dữ liệu và ảnh đã được cập nhật!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMsg", "Lỗi lưu dữ liệu: " + e.getMessage());
        }

        return "redirect:/asura/admin/comic/users/edit/" + id;
    }

    @GetMapping("/users/create")
    public String getUserCreatePage(Model model) {
        model.addAttribute("user", new UserCreateRequest());
        return "admin-templ/admin-user/create-user";
    }
    @PostMapping("/users/create")
    public String createUser(
            @ModelAttribute("user") UserCreateRequest request,
            Model model) {
        try {
            createUser.createUser(request);
            return "redirect:/asura/admin/comic/users/create?success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin-templ/admin-user/create-user";
        }
    }
    @PostMapping("/users/delete/{id}")
    @ResponseBody // Thêm cái này để trả về dữ liệu thay vì tìm giao diện HTML
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userDelete.deleteUser(id);
            // Trả về mã 200 OK kèm thông báo
            return ResponseEntity.ok("Xóa người dùng thành công");
        } catch (Exception e) {
            // Trả về mã lỗi 500 nếu có lỗi xảy ra
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi: " + e.getMessage());
        }
    }


    @GetMapping("/users/revenues")
    public String getRevenues(Model model,
                              @RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size) {

        model.addAttribute("activePage", "revenue");

        // --- Logic Phân trang ---
        if (page < 1) {
            page = 1; // Đảm bảo trang không bị âm
        }
        // Sắp xếp theo ngày tạo, mới nhất lên đầu
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        // Lấy dữ liệu giao dịch theo trang
        Page<Transaction> transactionsPage = transactionRepository.findAll(pageable);

        // --- Logic Thống kê (giữ nguyên) ---
        BigDecimal monthlyRevenue = revenueService.getMonthlyRevenue();
        BigDecimal todayRevenue = revenueService.getTodayRevenue();
        BigDecimal vipRevenue = revenueService.getVipRevenue();
        BigDecimal chapterRevenue = revenueService.getChapterRevenue();

        // --- Đưa dữ liệu vào Model ---
        model.addAttribute("transactionsPage", transactionsPage); // <-- Thay "transactions"
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("vipRevenue", vipRevenue);
        model.addAttribute("chapterRevenue", chapterRevenue);

        // Thêm thông tin phân trang
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionsPage.getTotalPages());
        model.addAttribute("totalItems", transactionsPage.getTotalElements());

        return "admin-templ/admin-user/revenues";
    }



    /* <!-- ================= interact ================= -->*/

    @GetMapping("/interact")
    public String getInteractAdmin() {
        return "admin-templ/admin-interaction/interact";
    }

    // báo cáo
    @GetMapping("/interact/report")
    public String getReport(Model model,
                            @RequestParam(name = "page", defaultValue = "1") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size) {

        if (page < 1) page = 1;

        // Sắp xếp theo trạng thái (PENDING lên đầu), sau đó là ngày tạo
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by("status").ascending()
                        .and(Sort.by("createdAt").descending()));

        Page<Report> reportPage = reportRepository.findAll(pageable);

        model.addAttribute("reportPage", reportPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reportPage.getTotalPages());
        model.addAttribute("activePage", "reports");
        return "admin-templ/admin-interaction/reports";
    }
    // bình luận
    /*@GetMapping("/comments")
    public String getComments(Model model) {
        model.addAttribute("activePage", "comments");
        return "admin-templ/comments";
    }*/
    // doah thu



    /**
     * Xử lý Xóa bình luận (Hard delete).
     */
    @GetMapping("/interact/comments")
    public String getComments(Model model,
                              @RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Comment> commentsPage = commentRepository.findAll(pageable);

        model.addAttribute("activePage", "comments");
        model.addAttribute("commentsPage", commentsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commentsPage.getTotalPages());
        model.addAttribute("totalItems", commentsPage.getTotalElements());

        return "admin-templ/admin-interaction/comments";
    }


   /* <!-- ================= PROFILE ================= -->*/
   @GetMapping("/profile")
   public String getUserAdminProfile() {
       return "admin-templ/USER_ADMIN_profile";
   }

    @GetMapping("/content/profile-admin-content")
    public String getContentAdminProfile() {
        return "admin-templ/admin-content/profile-admin-content";
    }

    @GetMapping("/users/profile-admin-user")
    public String getProfileAdminUser() {
        return "admin-templ/admin-user/profile-admin-user";
    }

    @GetMapping("/interact/profile-admin-interact")
    public String getInteractAdminProfile() {
        return "admin-templ/admin-interaction/profile-admin-interact";
    }

}
