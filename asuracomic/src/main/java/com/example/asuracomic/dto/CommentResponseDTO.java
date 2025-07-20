package com.example.asuracomic.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private boolean isEdited;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponseDTO> replies;
}
