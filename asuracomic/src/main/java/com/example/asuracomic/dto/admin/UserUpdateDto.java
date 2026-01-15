package com.example.asuracomic.dto.admin;

import com.example.asuracomic.model.enums.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class UserUpdateDto {
    private String username;
    private String email;
    private Role role;
    private boolean active;
    private boolean vipStatus;
    private BigDecimal coinBalance;
    private String description;
    private String newPassword;
    private String vipStartDate;
    private String vipExpiryDate;

}

