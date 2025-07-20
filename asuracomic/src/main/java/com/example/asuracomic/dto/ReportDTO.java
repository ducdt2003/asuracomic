package com.example.asuracomic.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportDTO {
    @NotBlank(message = "Lý do báo cáo không được để trống")
    private String reason;
}