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
        // tim kiếm usser theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Tài khoản hoặc mật khẩu không chính xác"));
        // Kiểm tra xem mật khẩu người dùng nhập có trùng với mật khẩu đã mã hóa trong database không
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
        // Lấy thông tin người dùng hiện tại từ session (người đang đăng nhập)
        UserDTO currentUserDTO = (UserDTO) session.getAttribute("currentUser");
        // Nếu chưa đăng nhập thì báo lỗi
        if (currentUserDTO == null) {
            throw new BadRequestException("Bạn chưa đăng nhập");
        }

        // Tìm người dùng trong cơ sở dữ liệu theo ID lấy từ session
        User user = userRepository.findById(currentUserDTO.getId())
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại"));

        // Kiểm tra xem mật khẩu hiện tại nhập vào có đúng với mật khẩu trong DB không
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu hiện tại không chính xác");
        }

        // Kiểm tra xem mật khẩu mới và mật khẩu xác nhận có trùng nhau không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        // Kiểm tra xem mật khẩu mới có đủ mạnh không (ví dụ: tối thiểu 8 ký tự, có chữ và số hoặc ký tự đặc biệt)
        if (!isValidPassword(request.getNewPassword())) {
            throw new BadRequestException("Mật khẩu mới không đáp ứng yêu cầu: tối thiểu 8 ký tự, ít nhất một chữ cái thường, một số hoặc ký tự đặc biệt");
        }

        // Nếu mọi thứ hợp lệ → mã hóa mật khẩu mới và cập nhật vào DB
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Cập nhật lại thông tin người dùng trong session (để session có dữ liệu mới nhất)
        session.setAttribute("currentUser", UserMapper.toDTO(user));
    }

    private boolean isValidPassword(String password) {
        // Kiểm tra độ dài tối thiểu: mật khẩu phải có ít nhất 8 ký tự
        if (password.length() < 8) {
            return false;
        }
        // Kiểm tra xem mật khẩu có chứa ít nhất một chữ cái thường (a–z) hay không
        // Sử dụng biểu thức chính quy: ".*[a-z].*" nghĩa là chuỗi có chứa ít nhất một ký tự thường
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        // Nếu thỏa cả 3 điều kiện trên, mật khẩu hợp lệ → trả về true
        if (!password.matches(".*[0-9\\W].*")) {
            return false;
        }
        return true;
    }
}