package com.miempresa.gestion_hotelera.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String usuario;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
}
