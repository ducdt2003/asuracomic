package com.example.asuracomic.config;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.model.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final HttpSession session;
// đăng nhập mới vào đươẹc
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            return false;
        }

        // Nếu là admin => chặn, vì admin không được phép vào trang bookmarks
        /*if (currentUser.getRole() == Role.ADMIN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Admin không được phép truy cập trang bookmarks.");
            return false;
        }*/
        return true;
    }
}
