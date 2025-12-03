package com.miempresa.gestion_hotelera.mapper;

import com.miempresa.gestion_hotelera.dto.HotelRequest;
import com.miempresa.gestion_hotelera.dto.HotelResponse;
import com.miempresa.gestion_hotelera.entity.Hotel;
import org.springframework.stereotype.Component;

@Component
public class HotelMapper {

    public Hotel toEntity(HotelRequest request) {
        return Hotel.builder()
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .ciudad(request.getCiudad())
                .pais(request.getPais())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .activo(request.getActivo())
                .build();
    }

    public HotelResponse toResponse(Hotel hotel) {
        return HotelResponse.builder()
                .id(hotel.getId())
                .nombre(hotel.getNombre())
                .direccion(hotel.getDireccion())
                .ciudad(hotel.getCiudad())
                .pais(hotel.getPais())
                .telefono(hotel.getTelefono())
                .email(hotel.getEmail())
                .activo(hotel.getActivo())
                .build();
    }
}
