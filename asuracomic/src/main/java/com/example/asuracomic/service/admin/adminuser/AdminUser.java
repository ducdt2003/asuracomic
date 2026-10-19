package com.example.asuracomic.service.admin.adminuser;

import com.example.asuracomic.dto.admin.UserUpdateDto;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminUser {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    public void updateUser(Long id, UserUpdateDto dto, MultipartFile avatarFile) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // ✅ CHỈ xử lý avatar khi có FILE
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(avatarFile);
            user.setAvatar(imageUrl);
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setCoinBalance(dto.getCoinBalance());
        user.setActive(dto.isActive());
        user.setVipStatus(dto.isVipStatus());
        user.setDescription(dto.getDescription());
        user.setUpdatedAt(LocalDateTime.now());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        if (dto.isVipStatus()) {
            if (dto.getVipStartDate() != null && !dto.getVipStartDate().isBlank()) {
                user.setVipStartDate(LocalDate.parse(dto.getVipStartDate()).atStartOfDay());
            }
            if (dto.getVipExpiryDate() != null && !dto.getVipExpiryDate().isBlank()) {
                user.setVipExpiryDate(LocalDate.parse(dto.getVipExpiryDate()).atStartOfDay());
            }
        }

        userRepository.save(user);
    }

}
