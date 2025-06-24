package com.example.asuracomic.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asura/admin")
public class adminController {
    @GetMapping
    public String purchase(){
        return "admin/admin-template";
    }
}
