package com.example.asuracomic.repository;

import com.example.asuracomic.entity.User;
import com.example.asuracomic.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
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

    boolean existsByEmail(String email); // thêm dòng này
    boolean existsByUsername(String username);
    boolean existsById(Long id);

    Optional<User> findByUsername(String username);


        @EntityGraph(attributePaths = {"transactions", "transactions.chapter", "transactions.vipConfig"})
        Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);

        @EntityGraph(attributePaths = {"transactions", "transactions.chapter", "transactions.vipConfig"})
        Page<User> findByVipStatus(boolean vipStatus, Pageable pageable);

        @EntityGraph(attributePaths = {"transactions", "transactions.chapter", "transactions.vipConfig"})
        Page<User> findByRole(Role role, Pageable pageable);

        @EntityGraph(attributePaths = {"transactions", "transactions.chapter", "transactions.vipConfig"})
        Page<User> findAll(Pageable pageable);

    Page<User> findByVipStatusAndVipExpiryDateBefore(boolean vipStatus, LocalDateTime expiryDate, Pageable pageable);




}