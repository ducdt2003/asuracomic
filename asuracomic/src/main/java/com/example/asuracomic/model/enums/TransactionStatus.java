package com.example.asuracomic.model.enums;

public enum TransactionStatus {
    SUCCESS,  // Giao dịch thành công, cập nhật số dư coin, trạng thái VIP, hoặc quyền truy cập chương trong bảng users/unlocked_chapters
    FAILED,   // Giao dịch thất bại, thông báo người dùng thử lại, không thay đổi số dư coin, trạng thái VIP, hoặc quyền truy cập
    CANCELLED
}
