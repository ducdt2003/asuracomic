package com.example.asuracomic.api.admin.adminuser;


import com.example.asuracomic.dto.admin.UserUpdateDto;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.exception.NotFoundException;
import com.example.asuracomic.model.enums.Role;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.service.DashboardService;
import com.example.asuracomic.service.admin.adminuser.UserDelete;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/asura/admin/users")
@RequiredArgsConstructor
public class AdminUserApi {
    private final DashboardService dashboardService;
    private final UserRepository userRepository;
    private final UserDelete userDelete;


/*
    Danh sách user
*/
    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean vipStatus,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        int pageSize = 10;
        page = Math.max(0, page);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        dashboardService.updateVipStatus();

        Page<User> usersPage =
                dashboardService.getUserPage(pageable, search, vipStatus, role);

        return ResponseEntity.ok(usersPage);
    }

  /*  Lấy chi tiết user để edit*/
  @GetMapping("/{id}")
  public ResponseEntity<?> getUserDetail(@PathVariable Long id) {
      User user = userRepository.findById(id)
              .orElseThrow(() -> new NotFoundException("usser không toonf tại"));

      return ResponseEntity.ok(user);
  }
  // xóa user
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {
      userDelete.deleteUser(id);
      return ResponseEntity.ok(Map.of(
              "message", "Xóa user thành công"
      ));
  }
}
