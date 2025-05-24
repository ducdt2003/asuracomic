package com.example.asuracomic.controller.web;

import com.example.asuracomic.dto.ComicCarouselDTO;
import com.example.asuracomic.dto.ComicTopDTO;
import com.example.asuracomic.dto.RelatedComicDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.service.ComicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/asura")
@RequiredArgsConstructor
public class WebController {
    private final ComicService comicService;
    private final ComicRepository comicRepository;
    // trang chủ
    @GetMapping
    public String homeLogin(  @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "50") int size,
                              Model model) {
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

        // Thêm vào model để hiển thị trên view
        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);





        Page<Comic> comicPage = comicService.getComicPage(page, size);
        model.addAttribute("comics", comicPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", comicPage.getTotalPages());

        return "web/web-main/home";
    }


    // trang chi tiết
    @GetMapping("/comic/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        // Lấy chi tiết truyện theo slug
        Comic comicDetail = comicService.getComicDetailsBySlug(slug);
        if (comicDetail == null) {
            // Xử lý trường hợp truyện không tồn tại
            return "redirect:/error"; // Hoặc chuyển hướng đến trang lỗi
        }
        model.addAttribute("comic", comicDetail);

        // Lấy danh sách chương
        List<Chapter> chapters = comicService.getChaptersByComic(comicDetail);
        Chapter firstChapter = comicService.getFirstChapter(comicDetail);
        Chapter latestChapter = comicService.getLatestChapter(comicDetail);

        model.addAttribute("chapters", chapters);
        model.addAttribute("firstChapter", firstChapter);
        model.addAttribute("latestChapter", latestChapter);

        // Lấy danh sách truyện liên quan
        //cung thể loại, cùng tác giả , cùng từ khóa
        List<RelatedComicDTO> relatedComics = comicService.getRelatedComics(comicDetail.getId(), 5);
        model.addAttribute("relatedComics", relatedComics);


        // Lấy danh sách top 10 cho tuần và tháng
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();

        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);

        return "web/web-main/detail";
    }


    // trang chapter
    @GetMapping("/comic/{comicSlug}/chapter/{chapterSlug}")
    public String chapter(@PathVariable String comicSlug, @PathVariable String chapterSlug, Model model) {
        // Lấy chi tiết truyện theo comicSlug
        Comic comic = comicService.getComicDetailsBySlug(comicSlug);
        if (comic == null) {
            return "redirect:/error";
        }

        // Lấy chương theo chapterSlug
        Chapter chapter = comicService.getChapterBySlug(comic, chapterSlug);
        if (chapter == null) {
            return "redirect:/error";
        }

        // Lấy danh sách truyện liên quan
        List<RelatedComicDTO> relatedComics = comicService.getRelatedComics(comic.getId(), 5);

        // Thêm dữ liệu vào model
        model.addAttribute("comic", comic);
        model.addAttribute("chapter", chapter);
        model.addAttribute("relatedComics", relatedComics);

        return "web/web-main/chapter";
    }



    // template
    @GetMapping("/series")
    public String series(Model model){
        // Lấy danh sách top 10 cho tuần, tháng, và tất cả thời gian
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();


        // Thêm vào model để hiển thị trên view
        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);

        return "web/web-templates/series";
    }

    @GetMapping("/bookmarks")
    public String bookmarks(Model model){
        // Lấy danh sách top 10 cho tuần, tháng, và tất cả thời gian
        List<ComicTopDTO> top10Weekly = comicService.getTop10CombinedWeekly();
        List<ComicTopDTO> top10Monthly = comicService.getTop10CombinedMonthly();


        // Thêm vào model để hiển thị trên view
        model.addAttribute("top10Weekly", top10Weekly);
        model.addAttribute("top10Monthly", top10Monthly);

        return "web/web-templates/bookmarks";
    }

    @GetMapping("/report")
    public String report(){
        return "web/web-templates/report";
    }

    @GetMapping("/authors")
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
