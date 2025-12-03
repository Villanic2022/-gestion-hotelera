package com.miempresa.gestion_hotelera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String usuario;
    private List<String> roles;
}
