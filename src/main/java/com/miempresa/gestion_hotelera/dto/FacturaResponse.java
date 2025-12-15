package com.miempresa.gestion_hotelera.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FacturaResponse {
    
    private Long id;
    private Long clienteId;
    private Long reservaId;
    private String tipoComprobante;
    private Integer puntoVenta;
    private Long numeroComprobante;
    private String cuitEmisor;
    private String documentoReceptor;
    private String tipoDocumentoReceptor;
    private LocalDateTime fechaEmision;
    private BigDecimal importeTotal;
    private String moneda;
    private String cae;
    private String caeVencimiento;
    private String estado;
    private String detalle;
}