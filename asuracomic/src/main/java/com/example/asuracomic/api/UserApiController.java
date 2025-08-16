package com.example.asuracomic.api;


import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.Transaction;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.entity.VipConfig;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.mapper.UserMapper;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.model.enums.TransactionType;
import com.example.asuracomic.repository.TransactionRepository;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.repository.VipConfigRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {
    private final VipConfigRepository vipConfigRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final HttpSession session;

    @PostMapping("/purchase-vip")
    @Transactional
    public ResponseEntity<Map<String, String>> purchaseVip(@RequestBody Map<String, String> request) {
        String slug = request.get("slug");
        UserDTO currentUserDTO = (UserDTO) session.getAttribute("currentUser");
        if (currentUserDTO == null) {
            throw new BadRequestException("Bạn chưa đăng nhập");
        }

        User user = userRepository.findById(currentUserDTO.getId())
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại"));

        VipConfig vipConfig = vipConfigRepository.findBySlug(slug)
                .orElseThrow(() -> new BadRequestException("Gói VIP không tồn tại"));

        if (!vipConfig.isActive()) { // Sử dụng isActive() thay vì getIsActive()
            throw new BadRequestException("Gói VIP này hiện không khả dụng");
        }

        if (user.getCoinBalance().compareTo(vipConfig.getCoinPrice()) < 0) {
            throw new BadRequestException("Số dư coin không đủ. Vui lòng nạp thêm coin.");
        }

        // Trừ coin
        user.setCoinBalance(user.getCoinBalance().subtract(vipConfig.getCoinPrice()));

        // Cập nhật VIP
        LocalDateTime now = LocalDateTime.now();
        user.setVipStartDate(now); // ngày bắt đầu VIP
        user.setVipExpiryDate(now.plusDays(vipConfig.getDurationDays())); // ngày hết hạn VIP
        user.setVipStatus(true);
        user.setUpdatedAt(now);
        userRepository.save(user);


        // Tạo giao dịch
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(TransactionType.VIP_PURCHASE);
        transaction.setAmount(vipConfig.getCoinPrice());
        transaction.setVipConfig(vipConfig);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionCode("TXN-" + System.currentTimeMillis());
        transaction.setCreatedAt(LocalDateTime.now());


        // Cập nhật session
        session.setAttribute("currentUser", UserMapper.toDTO(user));

        // **Thêm ngày mua và hết hạn VIP**
        transaction.setVipStartDate(now);
        transaction.setVipEndDate(now.plusDays(vipConfig.getDurationDays()));

        transactionRepository.save(transaction);
        // Trả về phản hồi với thông tin
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Mua gói VIP thành công!",
                "coinBalance", user.getCoinBalance().toString()
        ));
    }
}