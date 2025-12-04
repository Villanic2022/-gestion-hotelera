package com.miempresa.gestion_hotelera.mapper;

import com.miempresa.gestion_hotelera.dto.HuespedRequest;
import com.miempresa.gestion_hotelera.dto.HuespedResponse;
import com.miempresa.gestion_hotelera.entity.Huesped;
import org.springframework.stereotype.Component;

@Component
public class HuespedMapper {

    public Huesped toEntity(HuespedRequest request) {
        return Huesped.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .pais(request.getPais())
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .notas(request.getNotas())
                .build();
    }

    public HuespedResponse toResponse(Huesped entity) {
        return HuespedResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .email(entity.getEmail())
                .telefono(entity.getTelefono())
                .pais(entity.getPais())
                .tipoDocumento(entity.getTipoDocumento())
                .numeroDocumento(entity.getNumeroDocumento())
                .notas(entity.getNotas())
                .build();
    }
}
