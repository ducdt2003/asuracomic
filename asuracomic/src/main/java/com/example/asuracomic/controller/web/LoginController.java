package com.example.asuracomic.controller.web;

import com.example.asuracomic.entity.User;
import com.example.asuracomic.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/asura")
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "web/web-user/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        User user = userService.authenticate(email, password);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/asura";
        } else {
            model.addAttribute("error", "Sai email hoặc mật khẩu");
            return "web/web-user/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/asura/login";
    }
    @GetMapping("/register")
    public String register(){
        return "web/web-user/register";
    }
}
