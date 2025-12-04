package com.miempresa.gestion_hotelera.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TipoHabitacionResponse {

    private Long id;
    private Long hotelId;
    private String nombre;
    private String descripcion;
    private Integer capacidadBase;
    private Integer capacidadMax;
    private Boolean activo;
}