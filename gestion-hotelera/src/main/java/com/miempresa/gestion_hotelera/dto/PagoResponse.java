package com.miempresa.gestion_hotelera.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PagoResponse {

    private Long id;
    private BigDecimal monto;
    private String moneda;
    private String metodo;
    private boolean pagadoPorCanal;
    private String referenciaPago;
    private LocalDateTime fechaPago;
}
