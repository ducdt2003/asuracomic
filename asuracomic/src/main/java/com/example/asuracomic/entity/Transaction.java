package com.example.asuracomic.entity;


import com.example.asuracomic.model.enums.PaymentMethod;
import com.example.asuracomic.model.enums.TransactionStatus;
import com.example.asuracomic.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = @Index(name = "idx_user_id", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của giao dịch, dùng để tham chiếu

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng thực hiện giao dịch, liên kết để theo dõi lịch sử giao dịch

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType; // Loại giao dịch: COIN_PURCHASE, CHAPTER_UNLOCK, VIP_PURCHASE, xác định mục đích

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // Số tiền/coin, hiển thị chi tiết giao dịch trong lịch sử

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter; // Chương liên quan (nếu là CHAPTER_UNLOCK), liên kết để kiểm tra quyền truy cập

    @ManyToOne
    @JoinColumn(name = "vip_config_id")
    private VipConfig vipConfig; // Gói VIP liên quan (nếu là VIP_PURCHASE), xác định thời hạn và giá

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentMethod paymentMethod; // Phương thức thanh toán, hiển thị trong lịch sử, hỗ trợ đối chiếu tài chính

    @Column(unique = true, length = 50)
    private String transactionCode; // Mã giao dịch duy nhất, dùng để tra cứu hoặc đối chiếu với cổng thanh toán

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status; // Trạng thái: SUCCESS, FAILED, PENDING, hiển thị trong lịch sử, thông báo người dùng

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm tạo giao dịch, dùng để sắp xếp và thống kê giao dịch
}