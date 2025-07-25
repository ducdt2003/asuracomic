package com.example.asuracomic.mapper;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .coinBalance(user.getCoinBalance())
                .vipStatus(user.isVipStatus())
                .build();
        System.out.println("Mapped UserDTO: " + userDTO);
        return userDTO;
    }
}
