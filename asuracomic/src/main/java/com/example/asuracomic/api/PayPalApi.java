package com.example.asuracomic.api;


import com.example.asuracomic.entity.CoinPackage;
import com.example.asuracomic.exception.NotFoundException;
import com.example.asuracomic.repository.CoinPackageRepository;
import com.example.asuracomic.service.web.PayCoinService;
import com.example.asuracomic.service.web.PayPalService;
import com.paypal.orders.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
public class PayPalApi {

    @Autowired
    private CoinPackageRepository coinPackageRepository;
    @Autowired
    private PayPalService payPalService;
    @Autowired
    private PayCoinService payCoinService;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestParam Long packageId,
                                 HttpSession session) {

        if (session.getAttribute("currentUser") == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Chưa đăng nhập"));
        }

        CoinPackage pkg = coinPackageRepository.findById(packageId)
                .orElseThrow(() -> new NotFoundException("Gói coin không tồn tại"));
        try {
            // Chuyển đổi sang USD
            BigDecimal exchangeRate = new BigDecimal("25000");
            BigDecimal usd = pkg.getPrice().divide(exchangeRate, 2, RoundingMode.HALF_UP);

            if (usd.compareTo(BigDecimal.ZERO) <= 0) {
                // Có thể set mặc định là 0.01 USD để test hoặc báo lỗi
                usd = new BigDecimal("0.01");
            }

            Order order = payPalService.createOrder(
                    usd.doubleValue(),
                    "http://localhost:9090/api/paypal/success?packageId=" + packageId,
                    "http://localhost:9090/api/paypal/cancel"
            );

            for (var link : order.links()) {
                if ("approve".equals(link.rel())) {
                    return ResponseEntity.ok(
                            Map.of("approveUrl", link.href(), "orderId", order.id())
                    );
                }
            }
        } catch (IOException e) {
            // Xử lý lỗi khi kết nối PayPal thất bại
            e.printStackTrace(); // Log lỗi ra console để debug
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Lỗi kết nối đến hệ thống PayPal: " + e.getMessage()));
        }

        return ResponseEntity.status(500)
                .body(Map.of("message", "Không tạo được link PayPal"));

    }

    @GetMapping("/success")
    public ResponseEntity<?> success(@RequestParam("token") String orderId,
                                     @RequestParam Long packageId,
                                     HttpSession session) {
        try {
            var response = payPalService.captureOrder(orderId);

            if (!"COMPLETED".equals(response.result().status())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Thanh toán chưa hoàn tất"));
            }

            payCoinService.topUpCoin(packageId, session);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Nạp coin thành công",
                            "orderId", orderId
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Lỗi capture PayPal"));
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> cancel() {
        return ResponseEntity.ok(
                Map.of("message", "Người dùng hủy thanh toán")
        );
    }
}
