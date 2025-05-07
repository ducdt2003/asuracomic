package com.example.asuracomic.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asura")
public class CoinController {
    @GetMapping("/purchase")
    public String purchase(){
        return "web/web-coin/shop";
    }

    @GetMapping("/membership")
    public String membership(){
        return "web/web-coin/membership";
    }

    @GetMapping("/unlock")
    public String locker(){
        return "web/web-coin/unlock-chapter";
    }
}
