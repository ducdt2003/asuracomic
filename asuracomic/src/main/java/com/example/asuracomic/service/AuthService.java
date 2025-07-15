package com.example.asuracomic.service;

import com.example.asuracomic.entity.User;
import com.example.asuracomic.mapper.UserMapper;
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
}