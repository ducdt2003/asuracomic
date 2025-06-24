package com.example.asuracomic.controller.admin;

import com.example.asuracomic.dto.admin.DashboardStatsDTO;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/asura/admin")
@RequiredArgsConstructor
public class adminController {
    private final DashboardService dashboardService;
    
    @GetMapping // Xử lý yêu cầu GET tới /asura/admin
    public String homeAdmin(@RequestParam(defaultValue = "0") int page, Model model) { // Phương thức trả về trang dashboard, nhận Model để truyền dữ liệu
        DashboardStatsDTO stats = dashboardService.getDashboardStats(); // Lấy dữ liệu thống kê từ service
        model.addAttribute("stats", stats); // Thêm dữ liệu thống kê vào model để truyền tới view

        int pageSize = 5;
        Page<Comic> comicPage = dashboardService.getComicPage(PageRequest.of(page, pageSize));
        model.addAttribute("comics", comicPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", comicPage.getTotalPages());

        return "admin/admin-template"; // Trả về tên template Thymeleaf (admin/admin-template.html)
    }

}
