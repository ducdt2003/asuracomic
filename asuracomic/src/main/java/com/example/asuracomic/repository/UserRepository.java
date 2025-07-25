package com.example.asuracomic.repository;

import com.example.asuracomic.entity.User;
import com.example.asuracomic.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Định nghĩa interface UserRepository, mở rộng JpaRepository để cung cấp các phương thức thao tác với thực thể User
public interface UserRepository extends JpaRepository<User, Long> {
    // Phương thức tùy chỉnh để tìm kiếm một người dùng (User) dựa trên trường email
    // email là địa chỉ email duy nhất của người dùng (được định nghĩa trong thực thể User)
    // Trả về đối tượng User nếu tìm thấy, null nếu không tìm thấy
    // Được sử dụng để kiểm tra người dùng khi đăng nhập hoặc tìm kiếm thông tin người dùng qua email
    List<User> findByRole(Role userRole);

    Optional<User> findByEmail(String email);
}