package com.miempresa.gestion_hotelera.service;

import com.miempresa.gestion_hotelera.dto.DashboardDiaResponse;
import com.miempresa.gestion_hotelera.entity.EstadoReserva;
import com.miempresa.gestion_hotelera.entity.Habitacion;
import com.miempresa.gestion_hotelera.entity.Hotel;
import com.miempresa.gestion_hotelera.entity.Reserva;
import com.miempresa.gestion_hotelera.repository.HabitacionRepository;
import com.miempresa.gestion_hotelera.repository.HotelRepository;
import com.miempresa.gestion_hotelera.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final HotelRepository hotelRepository;
    private final HabitacionRepository habitacionRepository;
    private final ReservaRepository reservaRepository;

    @Transactional
    public DashboardDiaResponse resumenDia(Long hotelId, LocalDate fecha) {

        if (fecha == null) {
            fecha = LocalDate.now();
        }

        // Validar que exista el hotel
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel no encontrado"));

        // Habitaciones del hotel
        List<Habitacion> habitaciones = habitacionRepository.findByHotel_Id(hotelId);
        int habitacionesTotales = habitaciones.size();

        // Reservas del hotel
        List<Reserva> reservasHotel = reservaRepository.findByHotel_Id(hotelId);

        LocalDate finalFecha = fecha;

        // Check-ins del día (ignora canceladas)
        long checkinsHoy = reservasHotel.stream()
                .filter(r -> finalFecha.equals(r.getCheckIn()))
                .filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
                .count();

        // Check-outs del día (ignora canceladas)
        long checkoutsHoy = reservasHotel.stream()
                .filter(r -> finalFecha.equals(r.getCheckOut()))
                .filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
                .count();

        // Reservas nuevas creadas ese día
        long reservasNuevasHoy = reservasHotel.stream()
                .filter(r -> r.getCreadoEn() != null &&
                        r.getCreadoEn().toLocalDate().equals(finalFecha))
                .count();

        // Habitaciones ocupadas hoy:
        // rango: checkIn <= fecha < checkOut
        // estado: PENDIENTE, CONFIRMADA, CHECKIN (las que bloquean)
        int habitacionesOcupadas = (int) reservasHotel.stream()
                .filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
                .filter(r -> r.getCheckIn() != null && r.getCheckOut() != null)
                .filter(r -> !finalFecha.isBefore(r.getCheckIn()) && finalFecha.isBefore(r.getCheckOut()))
                .filter(r -> r.getEstado() == EstadoReserva.PENDIENTE
                        || r.getEstado() == EstadoReserva.CONFIRMADA
                        || r.getEstado() == EstadoReserva.CHECKIN)
                .map(Reserva::getHabitacion)
                .filter(h -> h != null && hotelId.equals(h.getHotel().getId()))
                .map(Habitacion::getId)
                .distinct()
                .count();

        double ocupacionPorcentaje = 0.0;
        if (habitacionesTotales > 0) {
            ocupacionPorcentaje = (habitacionesOcupadas * 100.0) / habitacionesTotales;
        }

        return DashboardDiaResponse.builder()
                .hotelId(hotel.getId())
                .fecha(finalFecha.toString())
                .habitacionesTotales(habitacionesTotales)
                .habitacionesOcupadas(habitacionesOcupadas)
                .ocupacionPorcentaje(ocupacionPorcentaje)
                .checkinsHoy(checkinsHoy)
                .checkoutsHoy(checkoutsHoy)
                .reservasNuevasHoy(reservasNuevasHoy)
                .build();
    }
}
