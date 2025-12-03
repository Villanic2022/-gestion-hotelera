package com.miempresa.gestion_hotelera.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HotelResponse {
    private Long id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String pais;
    private String telefono;
    private String email;
    private Boolean activo;
}
