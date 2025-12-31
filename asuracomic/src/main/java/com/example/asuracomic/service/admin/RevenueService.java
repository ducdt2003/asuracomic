package com.example.asuracomic.service.admin;



import com.example.asuracomic.entity.Transaction;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.model.enums.TransactionType;
import com.example.asuracomic.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public BigDecimal getMonthlyRevenue() {
        LocalDate now = LocalDate.now();
        return transactionRepository.findAll().stream()
                .filter(t -> t.getCreatedAt().getMonthValue() == now.getMonthValue()
                        && t.getCreatedAt().getYear() == now.getYear()
                        && t.getStatus() == TransactionStatus.SUCCESS)         // <-- enum compare
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTodayRevenue() {
        LocalDate now = LocalDate.now();
        return transactionRepository.findAll().stream()
                .filter(t -> t.getCreatedAt().toLocalDate().equals(now)
                        && t.getStatus() == TransactionStatus.SUCCESS)         // <-- enum compare
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getVipRevenue() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getTransactionType() == TransactionType.VIP_PURCHASE   // <-- enum compare
                        && t.getStatus() == TransactionStatus.SUCCESS)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getChapterRevenue() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getTransactionType() == TransactionType.CHAPTER_UNLOCK // <-- enum compare
                        && t.getStatus() == TransactionStatus.SUCCESS)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

