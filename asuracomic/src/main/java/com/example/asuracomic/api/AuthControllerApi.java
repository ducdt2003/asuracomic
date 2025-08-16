package com.example.asuracomic.api;


import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.model.request.ChangePasswordRequest;
import com.example.asuracomic.model.request.UpdateProfileRequest;
import com.example.asuracomic.service.AuthService;
import com.example.asuracomic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthControllerApi {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(request);
            return ResponseEntity.ok("Đổi mật khẩu thành công");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống, vui lòng thử lại sau");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            userService.updateProfile(request);
            return ResponseEntity.ok("Cập nhật thông tin thành công");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống, vui lòng thử lại sau");
        }
    }




}