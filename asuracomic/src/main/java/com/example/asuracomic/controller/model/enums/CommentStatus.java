package com.example.asuracomic.controller.model.enums;

public enum CommentStatus {
    ACTIVE,   // Bình luận hiển thị công khai trên trang chương
    REPORTED, // Bình luận bị báo cáo, chờ quản trị viên xem xét, có thể tạm ẩn
    DELETED   // Bình luận đã bị xóa bởi quản trị viên, không hiển thị
}
