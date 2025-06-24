package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Transaction;
import com.example.asuracomic.model.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = :status AND t.createdAt BETWEEN :startDate AND :endDate") // Truy vấn tính tổng tiền giao dịch theo trạng thái và thời gian
    BigDecimal sumAmountByStatusAndDateRange(@Param("status") TransactionStatus status, // Tham số trạng thái giao dịch
                                             @Param("startDate") LocalDateTime startDate, // Tham số thời điểm bắt đầu
                                             @Param("endDate") LocalDateTime endDate); // Tham số thời điểm kết thúc
}