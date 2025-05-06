package com.example.asuracomic.controller.model.enums;

public enum TransactionType {
    COIN_PURCHASE,  // Giao dịch mua coin, bổ sung số dư để người dùng mở khóa chương hoặc mua VIP
    CHAPTER_UNLOCK, // Giao dịch mở khóa chương, liên kết với unlocked_chapters để cấp quyền truy cập
    VIP_PURCHASE    // Giao dịch mua gói VIP, cập nhật trạng thái VIP cho người dùng
}
