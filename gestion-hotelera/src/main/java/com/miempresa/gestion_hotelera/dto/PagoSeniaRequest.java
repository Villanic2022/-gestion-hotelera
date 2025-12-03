package com.miempresa.gestion_hotelera.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoSeniaRequest {

    private BigDecimal monto;
    private String moneda;
    private String metodo;          // EFECTIVO, TRANSFERENCIA, TARJETA, etc.
    private String referenciaPago;  // nro de comprobante, etc.
}
