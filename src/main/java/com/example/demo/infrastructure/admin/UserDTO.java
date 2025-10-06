package com.example.demo.infrastructure.admin;

import lombok.Data;

@Data
public class UserDTO {

    private Integer id;
    private String nombre_usuario;
    private String email;
    private String password;
    private String role;
    private String status;
}
