package com.example.asuracomic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

    /*
     * CẤU HÌNH AN TOÀN DÙNG BCrypt.
     * Khi cần bật lại mã hóa mật khẩu:
     * 1. Comment bean passwordEncoder() lưu nguyên văn ở bên dưới.
     * 2. Bỏ comment bean BCrypt này.
     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    /*
     * CẤU HÌNH HIỆN TẠI: lưu và so sánh mật khẩu nguyên văn.
     * Chỉ nên dùng trong môi trường học tập/chạy cục bộ.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword == null ? null : rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String storedPassword) {
                return rawPassword != null && storedPassword != null
                        && rawPassword.toString().equals(storedPassword);
            }
        };
    }
}
