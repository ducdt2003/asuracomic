package com.example.asuracomic.service;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.CoinPackage;
import com.example.asuracomic.entity.Transaction;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.model.enums.TransactionType;
import com.example.asuracomic.repository.CoinPackageRepository;
import com.example.asuracomic.repository.TransactionRepository;
import com.example.asuracomic.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PayCoinService {

    private final UserRepository userRepository;
    private final CoinPackageRepository coinPackageRepository;
    private final TransactionRepository transactionRepository;

    public void topUpCoin(Long packageId, HttpSession session) {

        // 1. Kiểm tra đăng nhập
        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");
        if (currentUser == null)
            throw new BadRequestException("Bạn chưa đăng nhập");

        // 2. Lấy user
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại"));

        // 3. Lấy gói coin
        CoinPackage coinPackage = coinPackageRepository.findById(packageId)
                .orElseThrow(() -> new BadRequestException("Gói nạp không tồn tại"));

        if (!coinPackage.getActive())
            throw new BadRequestException("Gói nạp đã bị khóa");

        // 4. Cộng coin
        user.setCoinBalance(
                user.getCoinBalance().add(BigDecimal.valueOf(coinPackage.getCoin()))
        );
        userRepository.save(user);

        // 5. Lưu transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionType(TransactionType.COIN_PURCHASE)
                .amount(coinPackage.getPrice()) // tiền thật
                .status(TransactionStatus.SUCCESS)
                .transactionCode("COIN-" + UUID.randomUUID().toString().substring(0, 8))
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        // 6. Update session
        currentUser.setCoinBalance(user.getCoinBalance());
        session.setAttribute("currentUser", currentUser);
    }



}