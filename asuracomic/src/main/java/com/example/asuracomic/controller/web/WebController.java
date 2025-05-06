package com.example.asuracomic.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asura")
public class WebController {
    // trang chủ
    @GetMapping
    public String homeLogin() {
        return "web/web-main/home";
    }

    // trang chi tiết
    @GetMapping("/comic")
    public String detail() {
        return "web/web-main/detail";
    }

    // trang chapter
    @GetMapping("/comic/{comicId}/chapter/{chapterId}")
    public String chapter() {
        return "web/web-main/chapter";
    }



}
