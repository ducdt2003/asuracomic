package com.example.asuracomic.controller.web;

import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.model.request.LoginRequest;
import com.example.asuracomic.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/asura")
public class LoginController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "web/web-user/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        try {
            authService.login(request);
            return "redirect:/asura";
        } catch (BadRequestException ex) {
            model.addAttribute("error", ex.getMessage());
            return "web/web-user/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout();
        return "redirect:/asura/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "web/web-user/register";
    }
}
