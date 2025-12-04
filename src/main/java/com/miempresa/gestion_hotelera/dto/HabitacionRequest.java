package com.miempresa.gestion_hotelera.dto;

import lombok.Data;

@Data
public class HabitacionRequest {

    private Long hotelId;
    private Long tipoHabitacionId;
    private String codigo;
    private String piso;
    private String estado;
    private Boolean activo;
}
