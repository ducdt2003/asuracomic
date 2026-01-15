package com.example.asuracomic.service.admin.adminuser;

import com.example.asuracomic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDelete {
    private final UserRepository userRepository;

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        userRepository.deleteById(userId);
    }
}
