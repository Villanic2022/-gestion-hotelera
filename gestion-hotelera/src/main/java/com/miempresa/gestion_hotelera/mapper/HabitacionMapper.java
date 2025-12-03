package com.miempresa.gestion_hotelera.mapper;

import com.miempresa.gestion_hotelera.dto.HabitacionRequest;
import com.miempresa.gestion_hotelera.dto.HabitacionResponse;
import com.miempresa.gestion_hotelera.entity.Habitacion;
import com.miempresa.gestion_hotelera.entity.Hotel;
import com.miempresa.gestion_hotelera.entity.TipoHabitacion;
import org.springframework.stereotype.Component;

@Component
public class HabitacionMapper {

    public Habitacion toEntity(HabitacionRequest request, Hotel hotel, TipoHabitacion tipoHabitacion) {
        return Habitacion.builder()
                .hotel(hotel)
                .tipoHabitacion(tipoHabitacion)
                .codigo(request.getCodigo())
                .piso(request.getPiso())
                .estado(request.getEstado())
                .activo(request.getActivo())
                .build();
    }

    public HabitacionResponse toResponse(Habitacion entity) {
        return HabitacionResponse.builder()
                .id(entity.getId())
                .hotelId(entity.getHotel().getId())
                .tipoHabitacionId(entity.getTipoHabitacion().getId())
                .codigo(entity.getCodigo())
                .piso(entity.getPiso())
                .estado(entity.getEstado())
                .activo(entity.getActivo())
                .build();
    }
}
