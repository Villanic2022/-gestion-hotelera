package com.miempresa.gestion_hotelera.dto;

import lombok.Data;

@Data
public class TipoHabitacionRequest {

    private Long hotelId;
    private String nombre;
    private String descripcion;
    private Integer capacidadBase;
    private Integer capacidadMax;
    private Boolean activo;
}