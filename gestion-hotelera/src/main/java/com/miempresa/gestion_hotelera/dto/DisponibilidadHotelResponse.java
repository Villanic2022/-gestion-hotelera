package com.miempresa.gestion_hotelera.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DisponibilidadHotelResponse {

    private boolean tieneDisponibilidad;
    private int cantidadDisponibles;
    private List<HabitacionResponse> habitaciones;
}
