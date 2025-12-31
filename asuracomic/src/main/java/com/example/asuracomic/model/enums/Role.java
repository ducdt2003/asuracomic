package com.example.asuracomic.model.enums;

public enum Role {
    USER,               // Người dùng thông thường
    SUPER_ADMIN,        // Quản trị viên tối cao (có mọi quyền, quản lý các admin khác)
    CONTENT_ADMIN,      // Admin quản lý nội dung (Truyện, Chương, Tác giả, Thể loại)
    USER_ADMIN,         // Admin quản lý người dùng (Khóa tài khoản, xem thông tin user)
    INTERACTION_ADMIN,  // Admin quản lý tương tác (Bình luận, Báo cáo/Report, Đánh giá)
}
