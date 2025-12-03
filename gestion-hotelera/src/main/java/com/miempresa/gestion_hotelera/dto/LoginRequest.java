package com.miempresa.gestion_hotelera.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String usuario;
    private String password;
}
