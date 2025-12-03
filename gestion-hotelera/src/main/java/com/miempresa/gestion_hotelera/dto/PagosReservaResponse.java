package com.miempresa.gestion_hotelera.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PagosReservaResponse {

    private Long reservaId;
    private BigDecimal totalPagado;
    private BigDecimal precioTotal;
    private BigDecimal saldoPendiente;
    private String estadoPago;     // PENDIENTE / SENIADO / PAGADO
    private List<PagoResponse> pagos;
}
