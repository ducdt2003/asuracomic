package com.example.asuracomic.service;

import com.example.asuracomic.entity.User;
import com.example.asuracomic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // Tiêm BCryptPasswordEncoder

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.isActive() && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}