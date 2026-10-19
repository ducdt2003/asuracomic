package com.example.asuracomic.api;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.service.web.PayCoinService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/coin")
@RequiredArgsConstructor
public class CoinApi {

    private final PayCoinService payCoinService;
    private final HttpSession session;

    @PostMapping("/topup")
    public Map<String, Object> topup(@RequestParam Long packageId) {
        throw new UnsupportedOperationException("Vui lòng thanh toán qua cổng PayPal để nạp coin.");
    }
}
