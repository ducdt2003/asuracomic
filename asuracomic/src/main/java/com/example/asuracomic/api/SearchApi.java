package com.example.asuracomic.api;

import com.example.asuracomic.dto.SearchComicDTO;
import com.example.asuracomic.service.web.ComicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchApi {

    private final ComicService comicService;

    @GetMapping("/search")
    public ResponseEntity<List<SearchComicDTO>> searchComics(@RequestParam String query) {

        String trimmedQuery = query.trim();

        List<SearchComicDTO> result = comicService
                .findByTitleContainingIgnoreCase(trimmedQuery)
                .stream()
                .map(comic -> {
                    SearchComicDTO dto = new SearchComicDTO();
                    dto.setId(comic.getId());
                    dto.setTitle(comic.getTitle());
                    dto.setSlug(comic.getSlug());
                    dto.setCoverImage(comic.getCoverImage());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(result);
    }
}
