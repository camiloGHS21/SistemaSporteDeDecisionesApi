package com.example.demo.infrastructure.Auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
