package com.example.asuracomic.dto.admin;

import lombok.AllArgsConstructor; // Import để tạo constructor với tất cả thuộc tính
import lombok.Getter; // Import để tạo getter tự động
import lombok.NoArgsConstructor; // Import để tạo constructor rỗng
import lombok.Setter; // Import để tạo setter tự động

import java.math.BigDecimal; // Import lớp để lưu doanh thu
import java.text.NumberFormat; // Import lớp để định dạng số tiền
import java.util.Locale; // Import lớp để định dạng tiền Việt Nam

@Getter // Tạo getter cho tất cả thuộc tính
@Setter // Tạo setter cho tất cả thuộc tính
@NoArgsConstructor // Tạo constructor rỗng
@AllArgsConstructor // Tạo constructor với tất cả thuộc tính
public class DashboardStatsDTO { // Lớp DTO chứa dữ liệu thống kê
    private long totalComics; // Tổng số truyện
    private long totalUsers; // Tổng số người dùng
    private long pendingReports; // Số báo cáo đang chờ
    private BigDecimal monthlyRevenue; // Doanh thu tháng hiện tại

    public String getFormattedMonthlyRevenue() { // Phương thức định dạng doanh thu thành tiền Việt Nam
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")); // Tạo định dạng tiền Việt Nam
        return currencyFormat.format(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO); // Định dạng doanh thu, trả về 0 nếu null
    }
}
