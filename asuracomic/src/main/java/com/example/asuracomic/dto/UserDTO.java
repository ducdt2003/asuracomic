package com.example.asuracomic.dto;

import com.example.asuracomic.model.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    String email;
    String username;
    String avatar;
    Role role;
    BigDecimal coinBalance;
    boolean vipStatus;
}
