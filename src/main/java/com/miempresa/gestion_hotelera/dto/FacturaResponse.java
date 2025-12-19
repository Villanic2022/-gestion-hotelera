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

    private String tipoComprobante;      // "B", "X", "C"
    private Integer puntoVenta;
    private Long numeroComprobante;

    // Emisor
    private String emisorNombre;         // razón social / nombre de negocio
    private String emisorCuit;
    private String emisorDomicilioFiscal;

    // Receptor
    private String receptorNombre;       // huésped (nombre + apellido)
    private String tipoDocumentoReceptor;
    private String documentoReceptor;

    private LocalDateTime fechaEmision;
    private BigDecimal importeTotal;
    private String moneda;

    // AFIP
    private String cae;
    private String caeVencimiento;

    private String estado;               // APROBADA_AFIP / INTERNA / etc.
    private String detalle;

    // Conveniente para el front
    private boolean esFiscal;            // true si tiene CAE válido
}
