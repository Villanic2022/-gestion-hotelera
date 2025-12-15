package com.miempresa.gestion_hotelera.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FacturaRequest {
    private Long reservaId;
    private String tipoComprobante;   // "B" por ejemplo
    private Integer puntoVenta;       // 1, 2...
    private String tipoDocumento;     // "DNI" o "CUIT"
    private String documento;         // nro
    private BigDecimal importe;       // si no lo mandás, se puede tomar de la reserva
}
