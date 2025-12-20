package com.miempresa.gestion_hotelera.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    // Datos del usuario
    private String usuario;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
    
    // Datos fiscales de la empresa/cliente
    private String nombreEmpresa;
    private String cuit;
    private String condicionIva; // "MONOTRIBUTO", "RESPONSABLE_INSCRIPTO", "EXENTO", "CONSUMIDOR_FINAL"
    private String domicilioFiscal;
}
