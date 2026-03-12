package com.example.asuracomic.controller.web;

import com.example.asuracomic.entity.Transaction;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.model.enums.TransactionType;
import com.example.asuracomic.repository.TransactionRepository;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/asura/profile")
public class ProfileController {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/history")
    public String getTransactionHistory(Model model, HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");
        if (sessionUser == null) return "redirect:/asura/login";

        com.example.asuracomic.dto.UserDTO userDTO = (com.example.asuracomic.dto.UserDTO) sessionUser;
        User user = userRepository.findById(userDTO.getId()).orElseThrow();

        // CHỈ LẤY GIAO DỊCH NẠP XU
        List<Transaction> history = transactionRepository.findByUserAndTransactionTypeOrderByCreatedAtDesc(
                user,
                TransactionType.COIN_PURCHASE
        );

        model.addAttribute("history", history);
        return "web/web-coin/history";
    }
}