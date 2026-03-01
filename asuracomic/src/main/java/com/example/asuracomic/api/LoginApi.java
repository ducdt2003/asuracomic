package com.example.asuracomic.api;

import com.example.asuracomic.dto.additional.RegisterRequest;
import com.example.asuracomic.dto.additional.ResetPasswordRequest;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.model.request.LoginRequest;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.service.AuthService;
import com.example.asuracomic.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class LoginApi {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /* =========================
       LOGIN
     ========================= */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authService.login(request); // lưu user vào session
            return ResponseEntity.ok(
                    Map.of("message", "Đăng nhập thành công")
            );
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /* =========================
       LOGOUT
     ========================= */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(
                Map.of("message", "Đăng xuất thành công")
        );
    }

    /* =========================
       REGISTER
     ========================= */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String message = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getConfirmPassword()
        );

        if (message.startsWith("Đăng ký thành công")) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", message));
        }

        return ResponseEntity.badRequest()
                .body(Map.of("error", message));
    }

    /* =========================
       FORGOT PASSWORD
     ========================= */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email không tồn tại"));

        // 1. Tạo mã ngẫu nhiên (ví dụ 6 số)
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));

        // 2. Lưu vào DB và đặt hết hạn sau 5 phút
        user.setResetToken(otp);
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        // in ra Console
        System.out.println("MÃ OTP CỦA USER " + email + " LÀ: " + otp);

        return ResponseEntity.ok(Map.of("message", "Mã xác nhận đã được gửi đến email của bạn"));
    }

    /* =========================
       RESET PASSWORD
     ========================= */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        // 1. Kiểm tra khớp pass mới
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu xác nhận không khớp"));
        }

        // 2. Tìm user và kiểm tra OTP
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (user.getResetToken() == null || !user.getResetToken().equals(request.getOtp())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mã xác nhận (OTP) không chính xác"));
        }

        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mã xác nhận đã hết hạn"));
        }

        // 3. Đổi mật khẩu và XÓA mã OTP để không dùng lại được lần nữa
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }

}