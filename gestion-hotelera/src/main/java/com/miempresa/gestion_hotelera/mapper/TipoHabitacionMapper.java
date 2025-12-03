package com.miempresa.gestion_hotelera.mapper;

import com.miempresa.gestion_hotelera.dto.TipoHabitacionRequest;
import com.miempresa.gestion_hotelera.dto.TipoHabitacionResponse;
import com.miempresa.gestion_hotelera.entity.Hotel;
import com.miempresa.gestion_hotelera.entity.TipoHabitacion;
import org.springframework.stereotype.Component;

@Component
public class TipoHabitacionMapper {

    public TipoHabitacion toEntity(TipoHabitacionRequest request, Hotel hotel) {
        return TipoHabitacion.builder()
                .hotel(hotel)
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .capacidadBase(request.getCapacidadBase())
                .capacidadMax(request.getCapacidadMax())
                .activo(request.getActivo())
                .build();
    }

    public TipoHabitacionResponse toResponse(TipoHabitacion entity) {
        return TipoHabitacionResponse.builder()
                .id(entity.getId())
                .hotelId(entity.getHotel().getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .capacidadBase(entity.getCapacidadBase())
                .capacidadMax(entity.getCapacidadMax())
                .activo(entity.getActivo())
                .build();
    }
}