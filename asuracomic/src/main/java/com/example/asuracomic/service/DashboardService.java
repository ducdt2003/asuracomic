package com.example.asuracomic.service;


import com.example.asuracomic.dto.admin.DashboardStatsDTO;
import com.example.asuracomic.entity.*;
import com.example.asuracomic.model.enums.ReportStatus;
import com.example.asuracomic.model.enums.Role;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.asuracomic.entity.Comment; // đảm bảo import
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

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

    @Autowired // Tiêm phụ thuộc ReportRepository
    private CommentRepository commentRepository; // Biến để truy vấn dữ liệu báo cáo


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



    // user admin
    public Page<User> getUserPage(Pageable pageable, String search, Boolean vipStatus, Role role) {
        if (search != null && !search.isEmpty()) {
            return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
        }
        if (vipStatus != null) {
            return userRepository.findByVipStatus(vipStatus, pageable);
        }
        if (role != null) {
            return userRepository.findByRole(role, pageable);
        }
        return userRepository.findAll(pageable);
    }
    // danh sách user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Transactional
    public void updateVipStatus() {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 100); // Xử lý 100 người dùng mỗi lần
        Page<User> vipUsersPage;
        do {
            vipUsersPage = userRepository.findByVipStatusAndVipExpiryDateBefore(true, now, pageable);
            for (User user : vipUsersPage.getContent()) {
                user.setVipStatus(false);
                user.setVipExpiryDate(null);
                userRepository.save(user);
            }
            pageable = pageable.next();
        } while (vipUsersPage.hasNext());
    }



    // report admin
    public Page<Report> getReportPage(PageRequest pageRequest) {
        return reportRepository.findAll(pageRequest);
    }

    // Trong lớp DashboardService,
    public Page<Transaction> getTransactionPage(PageRequest pageRequest) {
        return transactionRepository.findAll(pageRequest);
    }

    // commets admin
    public Page<Comment> getCommentPage(PageRequest pageRequest) {
        return commentRepository.findAll(pageRequest);
    }

    // admin đường dẫn trang cchi tiết truyện slug
    public Optional<Comic> findBySlug(String slug) {
        return comicRepository.findBySlug(slug);
    }

    @Transactional
    public void deleteComic(Long comicId) {
        // Kiểm tra xem truyện có tồn tại không
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new IllegalArgumentException("Comic with ID " + comicId + " not found"));

        // Xóa truyện (các bản ghi liên quan sẽ tự động bị xóa do cascade = CascadeType.ALL)
        comicRepository.delete(comic);
    }




}