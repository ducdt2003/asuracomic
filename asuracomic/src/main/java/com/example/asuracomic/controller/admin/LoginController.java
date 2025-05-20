package com.example.asuracomic.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asura")
public class LoginController {
    @GetMapping("/login")
    public String login(){
        return "web/web-user/login";
    }
    @GetMapping("/register")
    public String register(){
        return "web/web-user/register";
    }
}
