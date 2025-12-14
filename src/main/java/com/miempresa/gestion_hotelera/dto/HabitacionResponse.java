package com.miempresa.gestion_hotelera.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class HabitacionResponse {

    private Long id;
    private Long hotelId;
    private Long tipoHabitacionId;
    private String codigo;
    private String piso;
    private String estado;
    private Boolean activo;
    private BigDecimal precioNoche;
}
