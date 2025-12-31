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
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final HttpSession session;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");

        // Chưa login
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        Role role = currentUser.getRole();
        String path = request.getRequestURI();

        // SUPER_ADMIN có toàn quyền
        if (role == Role.SUPER_ADMIN) {
            return true;
        }

        // Phân quyền theo module
        if (path.startsWith("/asura/admin/comic/content")) {
            return allow(role, Role.CONTENT_ADMIN, response);
        }

        if (path.startsWith("/asura/admin/comic/interact")) {
            return allow(role, Role.INTERACTION_ADMIN, response);
        }

        if (path.startsWith("/asura/admin/comic/users")) {
            return allow(role, Role.USER_ADMIN, response);
        }


        // Các trang admin khác → cấm
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }

    private boolean allow(Role role,
                          Role required,
                          HttpServletResponse response) throws Exception {
        if (role != required) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}

