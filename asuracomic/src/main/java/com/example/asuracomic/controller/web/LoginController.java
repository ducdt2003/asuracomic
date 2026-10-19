package com.example.asuracomic.controller.web;

import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.model.request.LoginRequest;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.service.web.AuthService;
import com.example.asuracomic.service.web.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/asura")
public class LoginController {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/login")
    public String loginPage() {
        return "web/web-user/login";
    }


    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        // tạo một đối tượng LoginRequest để chứa dữ liệu đăng nhập từ form
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        try {
            // Nếu đúng, authService.login() sẽ lưu thông tin người dùng vào session
            authService.login(request);
            // Nếu đăng nhập thành công → chuyển hướng người dùng về trang chủ "/asura"
            return "redirect:/asura";
        } catch (BadRequestException ex) {
            model.addAttribute("error", ex.getMessage());
            return "web/web-user/login";
        }
    }


    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "web/web-user/forgot-password"; // trả về view quên mật khẩu
    }
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        var userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email không tồn tại trong hệ thống");
            return "redirect:/asura/forgot-password";
        }

        // Tạo mã xác nhận OTP 6 số ngẫu nhiên
        String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));
        var user = userOptional.get();
        user.setResetToken(otp);
        user.setTokenExpiration(java.time.LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        System.out.println(">>> MÃ OTP XÁC NHẬN CHO EMAIL " + email + " LÀ: " + otp);

        redirectAttributes.addFlashAttribute("message", "Mã xác nhận OTP đã được gửi. (Môi trường Dev: kiểm tra console server hoặc mã là " + otp + ")");
        return "redirect:/asura/reset-password?email=" + email;
    }


    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "web/web-user/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("email") String email,
                                      @RequestParam("otp") String otp,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      RedirectAttributes redirectAttributes) {

        // 1. Kiểm tra mật khẩu khớp
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp. Vui lòng thử lại.");
            redirectAttributes.addAttribute("email", email);
            return "redirect:/asura/reset-password";
        }

        // 2. Tìm người dùng và kiểm tra OTP
        var userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng.");
            return "redirect:/asura/forgot-password";
        }
        var user = userOptional.get();

        if (user.getResetToken() == null || !user.getResetToken().equals(otp.trim())) {
            redirectAttributes.addFlashAttribute("error", "Mã xác nhận (OTP) không chính xác.");
            redirectAttributes.addAttribute("email", email);
            return "redirect:/asura/reset-password";
        }

        if (user.getTokenExpiration() == null || user.getTokenExpiration().isBefore(java.time.LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Mã xác nhận (OTP) đã hết hạn. Vui lòng lấy mã mới.");
            return "redirect:/asura/forgot-password";
        }

        // 3. Đổi mật khẩu thành công & xóa OTP
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);

        // 4. Chuyển hướng
        redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công! Hãy đăng nhập lại.");
        return "redirect:/asura/login";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout();
        return "redirect:/asura/login"; // chuyển hướng trực tiếp sang khác
    }

    @GetMapping("/register")
    public String registerPage() {
        return "web/web-user/register";
    }
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               Model model) {

       // Gọi service để xử lý logic đăng ký (kiểm tra trùng email, mã hóa mật khẩu, lưu vào DB,...)
        String message = userService.registerUser(username, email, password, confirmPassword);
        model.addAttribute("message", message);

        if (message.startsWith("Đăng ký thành công")) {
            return "redirect:/asura/login";
        }
        return "web/web-user/register";
    }
}
