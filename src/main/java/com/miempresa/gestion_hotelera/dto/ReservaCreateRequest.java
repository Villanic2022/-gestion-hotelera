package com.miempresa.gestion_hotelera.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReservaCreateRequest {

    private Long hotelId;
    private Long tipoHabitacionId;
    private Long habitacionId;           // opcional, se puede asignar después

    private Long huespedTitularId;       // huésped principal
    private List<Long> acompanianteIds;  // opcional

    private LocalDate checkIn;
    private LocalDate checkOut;

    private Integer adultos;
    private Integer ninos;

    private String canal;                // DIRECTO / BOOKING / etc.
    private BigDecimal precioTotal;
    private String moneda;

    private String comentariosCliente;
}
