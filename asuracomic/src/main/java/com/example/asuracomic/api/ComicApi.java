package com.example.asuracomic.api;

import com.example.asuracomic.dto.ComicDTO;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.model.enums.ComicStatus;
import com.example.asuracomic.model.enums.ComicType;
import com.example.asuracomic.service.ComicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/comics")
@RequiredArgsConstructor
public class ComicApi {

    private final ComicService comicService;

    @GetMapping("/series")
    public ResponseEntity<?> getSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String genre,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(defaultValue = "lastUpdated") String orderBy,
            @RequestParam(defaultValue = "") String query
    ) {
        Page<ComicDTO> comicPage =
                comicService.getComics(genre, status, type, orderBy, query, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("comics", comicPage.getContent());
        response.put("currentPage", comicPage.getNumber());
        response.put("totalPages", comicPage.getTotalPages());
        response.put("pageSize", size);

        response.put("top10Weekly", comicService.getTop10CombinedWeekly());
        response.put("top10Monthly", comicService.getTop10CombinedMonthly());

        response.put("statuses", ComicStatus.values());
        response.put("types", ComicType.values());

        return ResponseEntity.ok(response);
    }
}
