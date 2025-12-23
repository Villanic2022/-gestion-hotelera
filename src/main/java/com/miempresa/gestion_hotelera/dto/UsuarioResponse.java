package com.miempresa.gestion_hotelera.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UsuarioResponse {
    private Long id;
    private String usuario;
    private String nombre;
    private String apellido;
    private String email;
    private Boolean activo;
    private Set<String> roles;
}