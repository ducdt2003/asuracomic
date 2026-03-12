package com.example.asuracomic.api;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.mapper.UserMapper;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.service.VipService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/vip")
@RequiredArgsConstructor
public class VipApi {

    private final VipService vipService;
    private final HttpSession session;
    private final UserRepository userRepository;

    @PostMapping("/buy")
    public ResponseEntity<?> buyVip(@RequestParam String slug,
                                    HttpSession session) {


        // đăng hập ròi mươ có thng itnn
        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Vui lòng đăng nhập");
        }

        vipService.buyVipPackageApi(slug, currentUser.getId());

        // refresh session
        User user = userRepository.findById(currentUser.getId()).get();
        session.setAttribute("currentUser", UserMapper.toDTO(user));

        return ResponseEntity.ok("Mua VIP thành công");
        /*return ResponseEntity.ok(Map.of(
                "message", "Mua Vip thanh Cong",
                "vipExpryDate", user.getVipExpiryDate()
        ));*/
    }
}
