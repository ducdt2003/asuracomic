package com.example.asuracomic.service;


import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.mapper.UserMapper;
import com.example.asuracomic.model.enums.Role;
import com.example.asuracomic.model.request.UpdateProfileRequest;
import com.example.asuracomic.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;

    public void updateProfile(UpdateProfileRequest request) {
        // Lấy user hiện tại từ session
        UserDTO currentUserDTO = (UserDTO) session.getAttribute("currentUser");
        if (currentUserDTO == null) {
            throw new BadRequestException("Bạn chưa đăng nhập");
        }

        // Lấy user từ DB
        User user = userRepository.findById(currentUserDTO.getId())
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại"));

        // Cập nhật thông tin (chỉ description hoặc tất cả nếu muốn)
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        user.setDescription(request.getDescription());

        // Lưu lại
        userRepository.save(user);

        // Cập nhật session
        session.setAttribute("currentUser", UserMapper.toDTO(user));
    }


    public String registerUser(String username, String email, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp.";
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return "Email đã được sử dụng.";
        }
        if (userRepository.findByUsername(username).isPresent()) {
            return "Username đã được sử dụng.";
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .coinBalance(BigDecimal.ZERO)
                .vipStatus(false)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return "Đăng ký thành công! Hãy đăng nhập.";
    }
}
