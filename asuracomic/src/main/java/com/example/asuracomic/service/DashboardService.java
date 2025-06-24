package com.example.asuracomic.service;


import com.example.asuracomic.dto.admin.DashboardStatsDTO;
import com.example.asuracomic.entity.Comic;
import com.example.asuracomic.model.enums.ReportStatus;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.repository.ComicRepository;
import com.example.asuracomic.repository.ReportRepository;
import com.example.asuracomic.repository.TransactionRepository;
import com.example.asuracomic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service // Đánh dấu lớp này là service trong Spring
public class DashboardService {

    @Autowired // Tiêm phụ thuộc ComicRepository
    private ComicRepository comicRepository; // Biến để truy vấn dữ liệu truyện

    @Autowired // Tiêm phụ thuộc UserRepository
    private UserRepository userRepository; // Biến để truy vấn dữ liệu người dùng

    @Autowired // Tiêm phụ thuộc ReportRepository
    private ReportRepository reportRepository; // Biến để truy vấn dữ liệu báo cáo

    @Autowired // Tiêm phụ thuộc TransactionRepository
    private TransactionRepository transactionRepository; // Biến để truy vấn dữ liệu giao dịch

    public DashboardStatsDTO getDashboardStats() { // Phương thức lấy dữ liệu thống kê cho dashboard
        long totalComics = comicRepository.count(); // Đếm tổng số truyện trong cơ sở dữ liệu
        long totalUsers = userRepository.count(); // Đếm tổng số người dùng trong cơ sở dữ liệu
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING); // Đếm số báo cáo đang chờ

        // Tính doanh thu tháng hiện tại
        YearMonth currentMonth = YearMonth.now(); // Lấy tháng và năm hiện tại
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay(); // Thời điểm đầu tháng
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59); // Thời điểm cuối tháng
        BigDecimal monthlyRevenue = transactionRepository.sumAmountByStatusAndDateRange(
                TransactionStatus.SUCCESS, startOfMonth, endOfMonth); // Tính tổng doanh thu giao dịch thành công trong tháng

        return new DashboardStatsDTO(totalComics, totalUsers, pendingReports, monthlyRevenue); // Trả về DTO chứa thống kê
    }

    public Page<Comic> getComicPage(Pageable pageable) {
        return comicRepository.findAll(pageable);
    }





}