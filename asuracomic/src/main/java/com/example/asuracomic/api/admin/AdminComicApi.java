package com.example.asuracomic.api.admin;


import com.example.asuracomic.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/comics")
@RequiredArgsConstructor
public class AdminComicApi {

    private final DashboardService dashboardService;

    @DeleteMapping("/{comicId}")
    public ResponseEntity<String> deleteComic(@PathVariable Long comicId) {
        try {
            dashboardService.deleteComic(comicId);
            return ResponseEntity.ok("Comic with ID " + comicId + " has been deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while deleting the comic");
        }
    }

}
