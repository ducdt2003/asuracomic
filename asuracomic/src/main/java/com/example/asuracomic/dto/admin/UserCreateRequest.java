package com.example.asuracomic.dto.admin;

import com.example.asuracomic.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    private String username;
    private String email;
    private String password;   // ❗ BẮT BUỘC
    private Role role;         // ❗ enum
    private BigDecimal coinBalance;
    private Boolean vipStatus;
    private Boolean isActive;
    private String description;
    private String avatar;
    private LocalDate vipExpiryDate;
}
