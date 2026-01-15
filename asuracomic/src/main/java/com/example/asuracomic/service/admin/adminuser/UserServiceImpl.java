package com.example.asuracomic.service.admin.adminuser;

import com.example.asuracomic.dto.admin.UserCreateRequest;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.model.enums.Role;
import com.example.asuracomic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserCreateRequest request) {

        // Check email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Check username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .coinBalance(
                        request.getCoinBalance() != null
                                ? request.getCoinBalance()
                                : BigDecimal.ZERO
                )
                .vipStatus(Boolean.TRUE.equals(request.getVipStatus()))
                .isActive(Boolean.TRUE.equals(request.getIsActive()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

    }
}
