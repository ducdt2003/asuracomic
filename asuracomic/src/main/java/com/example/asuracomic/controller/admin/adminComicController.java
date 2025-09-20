package com.example.asuracomic.controller.admin;

import com.example.asuracomic.dto.admin.DashboardStatsDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.model.enums.Role;
import com.example.asuracomic.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/asura/admin/comic")
@RequiredArgsConstructor
public class adminComicController {
    private final DashboardService dashboardService;
    @GetMapping
    public String getIndexPage(Model model) {
        // Dashboard statistics
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        model.addAttribute("stats", stats);
        return "admin-templ/index";
    }

    @GetMapping("/detail")
    public String getDetailPage(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 5;
        Page<Comic> comicPage = dashboardService.getComicPage(PageRequest.of(page, pageSize));
        model.addAttribute("comics", comicPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", comicPage.getTotalPages());
        return "admin-templ/blog-index";
    }

    // update truyện vtair truyên lên
    @GetMapping("/create")
    public String getCreatePage() {
        return "admin-templ/blog-create";
    }

    // chi tiết truyện
    @GetMapping("/{slug}")
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
        return "admin-templ/blog-detail"; // file HTML
    }





    // chỉnh sữa truyện
    @GetMapping("/edit/{slug}")
    public String getEditPage(@PathVariable String slug, Model model) {
        Comic comic = dashboardService.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện"));
        model.addAttribute("comic", comic);
        return "admin-templ/blog-edit";
    }

    // Trang cập nhật nội dung chương
    @GetMapping("/chapter/update/{id}")
    public String getChapterUpdatePage() {
        return "admin-templ/chapter-update";
    }

    // Trang chỉnh sửa metadata / thông tin chương
    @GetMapping("/chapter/edit/{id}")
    public String getChapterEditPage() {
        return "admin-templ/chapters-edit";
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
        return "admin-templ/user";
    }

    @GetMapping("/users/edit/{id}")
    public String getUserEditPage() {
        return "admin-templ/edit-user";
    }

    @GetMapping("/users/create")
    public String getUserCreatePage() {
        return "admin-templ/create-user";
    }


}
