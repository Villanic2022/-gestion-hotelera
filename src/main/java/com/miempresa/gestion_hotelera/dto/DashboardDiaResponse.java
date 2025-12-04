package com.miempresa.gestion_hotelera.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDiaResponse {

    private Long hotelId;
    private String fecha;

    private int habitacionesTotales;
    private int habitacionesOcupadas;
    private double ocupacionPorcentaje;

    private long checkinsHoy;
    private long checkoutsHoy;
    private long reservasNuevasHoy;
}
