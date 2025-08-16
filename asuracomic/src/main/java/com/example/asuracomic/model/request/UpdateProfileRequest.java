package com.example.asuracomic.model.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    private String username;
    private String email;
    private String description;
}
