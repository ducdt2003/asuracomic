package com.example.asuracomic.service;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.Transaction;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.entity.VipConfig;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.model.enums.TransactionType;
import com.example.asuracomic.repository.TransactionRepository;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.repository.VipConfigRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VipService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VipConfigRepository vipConfigRepository;

    @Autowired
    private TransactionRepository transactionRepository;

/*    @Transactional
    public String buyVipPackage(String slug, HttpSession session) {
        // 1. Kiểm tra đăng nhập
        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");
        if (currentUser == null) throw new RuntimeException("Vui lòng đăng nhập.");

        // 2. Lấy thông tin gói VIP và User từ DB
        VipConfig config = vipConfigRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Gói VIP không tồn tại."));

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        // 3. Kiểm tra số dư
        if (user.getCoinBalance().compareTo(config.getCoinPrice()) < 0) {
            throw new RuntimeException("Số dư coin không đủ.");
        }

        // 4. Xử lý cộng dồn ngày VIP (Nếu đang là VIP thì cộng thêm, nếu không thì tính từ hiện tại)
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime currentExpiry = user.getVipExpiryDate();
        LocalDateTime newExpiry;

        if (user.isVipStatus() && currentExpiry != null && currentExpiry.isAfter(LocalDateTime.now())) {
            newExpiry = currentExpiry.plusDays(config.getDurationDays());
        } else {
            newExpiry = startDate.plusDays(config.getDurationDays());
        }

        // 5. Cập nhật User
        user.setCoinBalance(user.getCoinBalance().subtract(config.getCoinPrice()));
        user.setVipStatus(true);
        user.setVipStartDate(startDate);
        user.setVipExpiryDate(newExpiry);
        userRepository.save(user);

        // 6. Lưu lịch sử giao dịch
        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionType(TransactionType.VIP_PURCHASE)
                .amount(config.getCoinPrice())
                .vipConfig(config)
                .transactionCode("VIP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(TransactionStatus.SUCCESS)
                .vipStartDate(startDate)
                .vipEndDate(newExpiry)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        // 7. CẬP NHẬT SESSION (Quan trọng nhất để hiển thị ngay)
        refreshSession(user, session);

        return "/asura/membership";
    }

    private void refreshSession(User user, HttpSession session) {
        UserDTO dto = (UserDTO) session.getAttribute("currentUser");
        if (dto != null) {
            dto.setCoinBalance(user.getCoinBalance());
            dto.setVipStatus(user.isVipStatus());
            dto.setVipExpireAt(user.getVipExpiryDate());
            session.setAttribute("currentUser", dto);
        }
    }*/

    @Transactional
    public void buyVipPackageApi(String slug, Long userId) {

        // 1. Lấy gói VIP
        VipConfig config = vipConfigRepository.findBySlug(slug)
                .orElseThrow(() -> new BadRequestException("Gói VIP không tồn tại"));

        // 2. Lấy user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại"));

        // 3. Kiểm tra coin
        if (user.getCoinBalance().compareTo(config.getCoinPrice()) < 0) {
            throw new BadRequestException("Số dư coin không đủ");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime vipStart;
        LocalDateTime vipEnd;

        // 4. Xử lý VIP cộng dồn kiểm tra xem ngày hết hạn VIP của user có sau thời điểm hiện tại hay không.
        if (user.isVipStatus()
                && user.getVipExpiryDate() != null
                && user.getVipExpiryDate().isAfter(now)) {

            vipStart = user.getVipStartDate();
            vipEnd = user.getVipExpiryDate().plusDays(config.getDurationDays());

        } else {
            vipStart = now;
            vipEnd = now.plusDays(config.getDurationDays());
        }

        // 5. Update user
        user.setCoinBalance(user.getCoinBalance().subtract(config.getCoinPrice()));
        user.setVipStatus(true);
        user.setVipStartDate(vipStart);
        user.setVipExpiryDate(vipEnd);

        userRepository.save(user);

        // 6. Lưu transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionType(TransactionType.VIP_PURCHASE)
                .amount(config.getCoinPrice())
                .vipConfig(config)
                .transactionCode("VIP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(TransactionStatus.SUCCESS)
                .vipStartDate(vipStart)
                .vipEndDate(vipEnd)
                .createdAt(now)
                .build();

        transactionRepository.save(transaction);
    }
}