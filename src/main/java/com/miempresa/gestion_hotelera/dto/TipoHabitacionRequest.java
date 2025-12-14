package com.miempresa.gestion_hotelera.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TipoHabitacionRequest {

    private Long hotelId;
    private String nombre;
    private String descripcion;
    private Integer capacidadBase;
    private Integer capacidadMax;
    private Boolean activo;
    private BigDecimal precioNoche;
}