package com.example.asuracomic.controller.web;

import com.example.asuracomic.entity.CoinPackage;
import com.example.asuracomic.repository.CoinPackageRepository;
import com.example.asuracomic.service.PayCoinService;
import com.example.asuracomic.service.PayPalService;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
@RequestMapping("/asura/paypal")
public class PayPalController {

    @Autowired private PayPalService payPalService;
    @Autowired private CoinPackageRepository coinPackageRepository;
    @Autowired private PayCoinService payCoinService;

    @PostMapping("/pay")
    public String pay(@RequestParam Long packageId,
                      HttpSession session,
                      RedirectAttributes ra) {

        if (session.getAttribute("currentUser") == null) {
            return "redirect:/asura/login";
        }

        try {
            CoinPackage pkg = coinPackageRepository.findById(packageId)
                    .orElseThrow(() -> new RuntimeException("Gói coin không tồn tại"));

            BigDecimal usd = pkg.getPrice()
                    .divide(new BigDecimal("25000"), 2, RoundingMode.HALF_UP);

            Order order = payPalService.createOrder(
                    usd.doubleValue(),
                    "http://localhost:9090/asura/paypal/success?packageId=" + packageId,
                    "http://localhost:9090/asura/paypal/cancel"
            );

            // 🔥 THÊM ĐOẠN NÀY
            for (var link : order.links()) {
                if ("approve".equals(link.rel())) {
                    return "redirect:" + link.href();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("errorMessage", "Không thể tạo giao dịch PayPal");
        }

        return "redirect:/asura/coin";
    }

    @GetMapping("/success")
    public String success(@RequestParam("token") String orderId,
                          @RequestParam Long packageId,
                          HttpSession session,
                          RedirectAttributes ra) {
        try {
            var response = payPalService.captureOrder(orderId);
            if (response.result().status().equals("COMPLETED")) {
                // GỌI SERVICE CỘNG COIN VÀO DB TẠI ĐÂY
                payCoinService.topUpCoin(packageId, session);
                ra.addFlashAttribute("successMessage", "Nạp coin thành công qua PayPal!");
                return "redirect:/asura/coin";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/asura/coin?error=capture_failed";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "redirect:/asura/coin?error=user_cancelled";
    }
}
