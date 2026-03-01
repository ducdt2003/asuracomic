package com.example.asuracomic.mapper;

import com.example.asuracomic.dto.ComicDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.Comic;


public class ComicMapper {

    public static ComicDTO fromEntity(Comic comic) {

        Chapter latestChapter = null;
        if (comic.getChapters() != null && !comic.getChapters().isEmpty()) {
            latestChapter = comic.getChapters().get(0);
        }

        return ComicDTO.builder()
                .title(comic.getTitle())
                .slug(comic.getSlug())
                .coverImage(comic.getCoverImage())

                .status(comic.getStatus().name())
                .type(comic.getType().name())

                .latestChapterNumber(
                        latestChapter != null ? latestChapter.getChapterNumber() : null
                )
                .latestChapterTitle(
                        latestChapter != null ? latestChapter.getTitle() : null
                )
                .latestChapterSlug(
                        latestChapter != null ? latestChapter.getSlug() : null
                )

                .averageRating(
                        comic.getAverageRating() != null
                                ? comic.getAverageRating().floatValue()
                                : 0f
                )
                .build();
    }
}