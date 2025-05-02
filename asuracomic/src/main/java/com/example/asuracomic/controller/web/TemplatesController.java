package com.example.asuracomic.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("asura")
public class TemplatesController {
    @GetMapping("/series")
    public String series(){
        return "web/web-templates/series";
    }

    @GetMapping("/bookmarks")
    public String bookmarks(){
        return "web/web-templates/bookmarks";
    }

    @GetMapping("/report")
    public String report(){
        return "web/web-templates/report";
    }
}
