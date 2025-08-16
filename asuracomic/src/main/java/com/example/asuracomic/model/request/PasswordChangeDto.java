package com.example.asuracomic.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordChangeDto {
    @NotBlank(message = "Mật khẩu hiện tại là bắt buộc")
    private String currentPassword;

    @NotBlank(message = "Mật khẩu mới là bắt buộc")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu là bắt buộc")
    private String confirmPassword;
}