package com.miempresa.gestion_hotelera.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReservaResponse {

    private Long id;

    private Long hotelId;
    private Long tipoHabitacionId;
    private Long habitacionId;

    private Long huespedTitularId;
    private List<Long> acompanianteIds;

    private LocalDate checkIn;
    private LocalDate checkOut;

    private Integer adultos;
    private Integer ninos;

    private String estadoPago;
    private String canal;
    private String estado;

    private BigDecimal precioTotal;
    private String moneda;

    private String comentariosCliente;
    private String comentariosInternos;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
