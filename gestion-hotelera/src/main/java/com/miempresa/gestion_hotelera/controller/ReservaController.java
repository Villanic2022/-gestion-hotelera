package com.miempresa.gestion_hotelera.controller;

import com.miempresa.gestion_hotelera.dto.*;
import com.miempresa.gestion_hotelera.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.miempresa.gestion_hotelera.dto.PagosReservaResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@RequestBody ReservaCreateRequest request) {
        return ResponseEntity.ok(reservaService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listar() {
        return ResponseEntity.ok(reservaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtener(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

    @PutMapping("/{id}/checkin")
    public ResponseEntity<ReservaResponse> checkin(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.checkin(id));
    }

    @PutMapping("/{id}/checkout")
    public ResponseEntity<ReservaResponse> checkout(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.checkout(id));
    }

    // Ejemplo de endpoint de disponibilidad
    @GetMapping("/disponibilidad/habitacion")
    public ResponseEntity<Boolean> disponibilidadHabitacion(
            @RequestParam Long habitacionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut
    ) {
        return ResponseEntity.ok(reservaService.habitacionDisponible(habitacionId, checkIn, checkOut));
    }
    @GetMapping("/disponibilidad/hotel")
    public ResponseEntity<List<HabitacionResponse>> disponibilidadPorHotel(
            @RequestParam Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut
    ) {
        return ResponseEntity.ok(reservaService.disponibilidadHotel(hotelId, checkIn, checkOut));
    }

    @GetMapping("/disponibilidad/hotel/resumen")
    public ResponseEntity<DisponibilidadHotelResponse> disponibilidadPorHotelResumen(
            @RequestParam Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut
    ) {
        return ResponseEntity.ok(reservaService.disponibilidadHotelResumen(hotelId, checkIn, checkOut));
    }

    @PostMapping("/{id}/senia")
    public ResponseEntity<ReservaResponse> registrarSenia(
            @PathVariable Long id,
            @RequestBody PagoSeniaRequest request
    ) {
        return ResponseEntity.ok(reservaService.registrarSenia(id, request));
    }

    @GetMapping("/{id}/pagos")
    public ResponseEntity<PagosReservaResponse> listarPagos(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.listarPagosReserva(id));
    }

}
