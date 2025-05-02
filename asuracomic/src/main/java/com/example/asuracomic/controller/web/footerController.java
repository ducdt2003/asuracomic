package com.example.asuracomic.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asura")
public class footerController {
    @GetMapping("/privacy-policy")
    public String privacyPolicy(){
        return "web/web-footer/privacy-policy";
    }

    @GetMapping("/dmcs-notice")
    public String dmcsNotice(){
        return "web/web-footer/dmcs-notice";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService(){
        return "web/web-footer/terms-of-service";
    }
}
