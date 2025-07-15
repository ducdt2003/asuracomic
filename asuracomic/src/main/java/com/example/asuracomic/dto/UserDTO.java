package com.example.asuracomic.dto;

import com.example.asuracomic.model.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    String username;
    String displayName;
    String email;
    String avatar;
    String phone;
    Role role;
}
