/*
package com.example.asuracomic.controller.admin;

import com.example.asuracomic.dto.admin.DashboardStatsDTO;
import com.example.asuracomic.entity.*;
import com.example.asuracomic.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/asura/admin")
@RequiredArgsConstructor
public class adminController {
    private final DashboardService dashboardService;


    @GetMapping
    public String homeAdmin(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(name = "userPage", defaultValue = "0") int userPage,
                            @RequestParam(defaultValue = "0") int reportPage,
                            @RequestParam(defaultValue = "0") int transactionPage,
                            @RequestParam(defaultValue = "0") int commentPage,
                            @RequestParam(defaultValue = "5") int size,
                            Model model) {

        // Dashboard statistics
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        model.addAttribute("stats", stats);

        // Comic pagination
        int pageSize = 5;
        Page<Comic> comicPage = dashboardService.getComicPage(PageRequest.of(page, pageSize));
        model.addAttribute("comics", comicPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", comicPage.getTotalPages());

        // User pagination
        Page<User> userPageResult = dashboardService.getUserPage(PageRequest.of(userPage, pageSize));
        model.addAttribute("users", userPageResult.getContent());
        model.addAttribute("currentUserPage", userPage);
        model.addAttribute("totalUserPages", userPageResult.getTotalPages());


        // report admin
        Page<Report> reportPageResult = dashboardService.getReportPage(PageRequest.of(reportPage, size));
        model.addAttribute("reports", reportPageResult.getContent());
        model.addAttribute("currentReportPage", reportPage);
        model.addAttribute("totalReportPages", reportPageResult.getTotalPages());

        // Giao dịch (transactions)
        Page<Transaction> transactions = dashboardService.getTransactionPage(PageRequest.of(transactionPage, size));
        model.addAttribute("transactions", transactions.getContent());
        model.addAttribute("currentTransactionPage", transactionPage);
        model.addAttribute("totalTransactionPages", transactions.getTotalPages());

        // comments admin
        Page<Comment> commentPageResult = dashboardService.getCommentPage(PageRequest.of(commentPage, size));
        model.addAttribute("comments", commentPageResult.getContent());
        model.addAttribute("currentCommentPage", commentPage);
        model.addAttribute("totalCommentPages", commentPageResult.getTotalPages());
        return "admin/admin-template";
    }




}
*/
