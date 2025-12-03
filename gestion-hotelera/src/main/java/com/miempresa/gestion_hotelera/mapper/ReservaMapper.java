package com.miempresa.gestion_hotelera.mapper;

import com.miempresa.gestion_hotelera.dto.ReservaCreateRequest;
import com.miempresa.gestion_hotelera.dto.ReservaResponse;
import com.miempresa.gestion_hotelera.entity.*;
import org.springframework.stereotype.Component;
import com.miempresa.gestion_hotelera.entity.EstadoReserva;
import com.miempresa.gestion_hotelera.entity.EstadoPago;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservaMapper {

    public Reserva toEntity(ReservaCreateRequest req,
                            Hotel hotel,
                            TipoHabitacion tipo,
                            Habitacion habitacion) {

        Reserva reserva = Reserva.builder()
                .hotel(hotel)
                .tipoHabitacion(tipo)
                .habitacion(habitacion)
                .canal(req.getCanal() != null ? req.getCanal() : "DIRECTO")
                .idExterno(null)
                .checkIn(req.getCheckIn())
                .checkOut(req.getCheckOut())
                .adultos(req.getAdultos())
                .ninos(req.getNinos())
                .estadoPago(EstadoPago.PENDIENTE)
                .estado(EstadoReserva.PENDIENTE)
                .precioTotal(req.getPrecioTotal())
                .moneda(req.getMoneda())
                .comentariosCliente(req.getComentariosCliente())
                .comentariosInternos(null)
                .build();

        return reserva;
    }

    public ReservaResponse toResponse(Reserva reserva,
                                      Long huespedTitularId,
                                      List<Long> acompanianteIds) {

        return ReservaResponse.builder()
                .id(reserva.getId())
                .hotelId(reserva.getHotel().getId())
                .tipoHabitacionId(reserva.getTipoHabitacion().getId())
                .habitacionId(reserva.getHabitacion() != null ? reserva.getHabitacion().getId() : null)
                .huespedTitularId(huespedTitularId)
                .acompanianteIds(acompanianteIds)
                .checkIn(reserva.getCheckIn())
                .checkOut(reserva.getCheckOut())
                .adultos(reserva.getAdultos())
                .ninos(reserva.getNinos())
                .canal(reserva.getCanal())
                .estado(reserva.getEstado() != null ? reserva.getEstado().name() : null)
                .estadoPago(reserva.getEstadoPago() != null ? reserva.getEstadoPago().name() : null)
                .precioTotal(reserva.getPrecioTotal())
                .moneda(reserva.getMoneda())
                .comentariosCliente(reserva.getComentariosCliente())
                .comentariosInternos(reserva.getComentariosInternos())
                .creadoEn(reserva.getCreadoEn())
                .actualizadoEn(reserva.getActualizadoEn())
                .build();
    }


}
