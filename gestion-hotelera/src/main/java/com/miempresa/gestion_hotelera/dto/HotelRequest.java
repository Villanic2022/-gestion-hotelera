package com.miempresa.gestion_hotelera.dto;

import lombok.Data;

@Data
public class HotelRequest {
    private String nombre;
    private String direccion;
    private String ciudad;
    private String pais;
    private String telefono;
    private String email;
    private Boolean activo;
}
