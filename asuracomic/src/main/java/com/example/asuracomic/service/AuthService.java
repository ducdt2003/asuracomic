package com.example.asuracomic.service;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.mapper.UserMapper;
import com.example.asuracomic.model.request.ChangePasswordRequest;
import com.example.asuracomic.model.request.LoginRequest;
import com.example.asuracomic.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import com.example.asuracomic.exception.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final HttpSession session;

    public void login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Tài khoản hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Tài khoản hoặc mật khẩu không chính xác");
        }

        // Luu lai: session, cookie, database, redis, ...
        session.setAttribute("currentUser", UserMapper.toDTO(user));
    }

    public void logout() {
        session.removeAttribute("currentUser");
    }


    public void changePassword(ChangePasswordRequest request) {
        // Get the current user from session
        UserDTO currentUserDTO = (UserDTO) session.getAttribute("currentUser");
        if (currentUserDTO == null) {
            throw new BadRequestException("Bạn chưa đăng nhập");
        }

        // Find the user in the database
        User user = userRepository.findById(currentUserDTO.getId())
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu hiện tại không chính xác");
        }

        // Validate new password and confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        // Validate password requirements
        if (!isValidPassword(request.getNewPassword())) {
            throw new BadRequestException("Mật khẩu mới không đáp ứng yêu cầu: tối thiểu 8 ký tự, ít nhất một chữ cái thường, một số hoặc ký tự đặc biệt");
        }

        // Encode and update the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Update session with the latest user data
        session.setAttribute("currentUser", UserMapper.toDTO(user));
    }

    private boolean isValidPassword(String password) {
        // Minimum 8 characters
        if (password.length() < 8) {
            return false;
        }

        // At least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // At least one number, symbol, or whitespace
        if (!password.matches(".*[0-9\\W].*")) {
            return false;
        }

        return true;
    }
}