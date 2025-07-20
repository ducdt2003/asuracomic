package com.example.asuracomic.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {
    private Long chapterId;
    private Long parentCommentId;
    @NotBlank(message = "Nội dung không được để trống")
    private String content;
}
