package com.example.asuracomic.controller.admin;

import com.example.asuracomic.dto.admin.DashboardStatsDTO;
import com.example.asuracomic.dto.admin.UserUpdateDto;
import com.example.asuracomic.entity.*;
import com.example.asuracomic.model.enums.Role;
import com.example.asuracomic.repository.CommentRepository;
import com.example.asuracomic.repository.ReportRepository;
import com.example.asuracomic.repository.TransactionRepository;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.service.DashboardService;
import com.example.asuracomic.service.admin.adminuser.AdminUser;
import com.example.asuracomic.service.admin.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

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
    private final AdminUser adminUser;
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
        // Lấy danh sách chương có phân trang
        Page<Chapter> chapterPage = dashboardService.getChaptersByComic(comic, page, size);
        model.addAttribute("comic", comic);
        model.addAttribute("chapterPage", chapterPage);
        return "admin-templ/admin-content/blog-edit";
    }

    // Trang cập nhật nội dung chương
    @GetMapping("/content/chapter/update/{id}")
    public String getChapterUpdatePage() {
        return "admin-templ/admin-content/chapter-update";
    }

    // Trang chỉnh sửa metadata / thông tin chương
    @GetMapping("/content/chapter/edit/{id}")
    public String getChapterEditPage() {
        return "admin-templ/admin-content/chapters-edit";
    }

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
    public String getUserCreatePage() {
        return "admin-templ/admin-user/create-user";
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
