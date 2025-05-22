package com.example.asuracomic.controller.web;

import com.example.asuracomic.dto.ComicCarouselDTO;
import com.example.asuracomic.dto.ComicTopDTO;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.service.ComicService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/asura")
@RequiredArgsConstructor
public class WebController {
    private final ComicService comicService;
    // trang chủ
    @GetMapping
    public String homeLogin(Model model) {
        // top comics rating
        List<ComicCarouselDTO> hotComics = comicService.getHotComicsForCarousel();
        model.addAttribute("hotComics", hotComics);
        // top comics views today
        List<Comic> popularToday = comicService.getTopViewedComicsToday(5);
        model.addAttribute("popularToday", popularToday);
        // bảng xếp hạng
        // Lấy danh sách top 10 cho tuần, tháng, và tất cả thời gian
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();
        List<ComicTopDTO> top10All = comicService.getTop10CombinedAll();

        // Thêm vào model để hiển thị trên view
        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);
        model.addAttribute("top10All", top10All);
        return "web/web-main/home";
    }

    // trang chi tiết
    @GetMapping("/comic")
    public String detail(Model model) {
        // Lấy danh sách top 10 cho tuần, tháng, và tất cả thời gian
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();
        List<ComicTopDTO> top10All = comicService.getTop10CombinedAll();

        // Thêm vào model để hiển thị trên view
        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);
        model.addAttribute("top10All", top10All);
        return "web/web-main/detail";
    }

    // trang chapter
    @GetMapping("/comic/{comicId}/chapter/{chapterId}")
    public String chapter() {
        return "web/web-main/chapter";
    }


    // template
    @GetMapping("/series")
    public String series(Model model){
        // Lấy danh sách top 10 cho tuần, tháng, và tất cả thời gian
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();
        List<ComicTopDTO> top10All = comicService.getTop10CombinedAll();

        // Thêm vào model để hiển thị trên view
        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);
        model.addAttribute("top10All", top10All);
        return "web/web-templates/series";
    }

    @GetMapping("/bookmarks")
    public String bookmarks(Model model){
        // Lấy danh sách top 10 cho tuần, tháng, và tất cả thời gian
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();
        List<ComicTopDTO> top10All = comicService.getTop10CombinedAll();

        // Thêm vào model để hiển thị trên view
        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);
        model.addAttribute("top10All", top10All);
        return "web/web-templates/bookmarks";
    }

    @GetMapping("/report")
    public String report(){
        return "web/web-templates/report";
    }

    @GetMapping("/author")
    public String author(){
        return "web/web-templates/author-template";
    }



    // chân trang
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
