package com.example.asuracomic.repository;

import com.example.asuracomic.entity.Transaction;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = :status AND t.createdAt BETWEEN :startDate AND :endDate") // Truy vấn tính tổng tiền giao dịch theo trạng thái và thời gian
    BigDecimal sumAmountByStatusAndDateRange(@Param("status") TransactionStatus status, // Tham số trạng thái giao dịch
                                             @Param("startDate") LocalDateTime startDate, // Tham số thời điểm bắt đầu
                                             @Param("endDate") LocalDateTime endDate); // Tham số thời điểm kết thúc

    @Modifying
    @Query("UPDATE Transaction t SET t.status = :status WHERE t.chapter.id IN " +
            "(SELECT c.id FROM Chapter c WHERE c.comic.id = :comicId)")
    void updateStatusByComicId(Long comicId, TransactionStatus status);


    @EntityGraph(attributePaths = {"chapter", "vipConfig", "user"})
    Page<Transaction> findByUserIdAndTransactionTypeIn(Long userId, List<TransactionType> transactionTypes, Pageable pageable);


    @EntityGraph(attributePaths = {"chapter", "vipConfig", "user"})
    Page<Transaction> findByUserIdAndTransactionType(Long userId, TransactionType transactionType, Pageable pageable);
}