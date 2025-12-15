package com.miempresa.gestion_hotelera.mapper;

import com.miempresa.gestion_hotelera.dto.FacturaResponse;
import com.miempresa.gestion_hotelera.entity.Factura;
import org.springframework.stereotype.Component;

@Component
public class FacturaMapper {

    public FacturaResponse toResponse(Factura entity) {
        return FacturaResponse.builder()
                .id(entity.getId())
                .clienteId(entity.getCliente() != null ? entity.getCliente().getId() : null)
                .reservaId(entity.getReserva() != null ? entity.getReserva().getId() : null)
                .tipoComprobante(entity.getTipoComprobante())
                .puntoVenta(entity.getPuntoVenta())
                .numeroComprobante(entity.getNumeroComprobante())
                .cuitEmisor(entity.getCuitEmisor())
                .documentoReceptor(entity.getDocumentoReceptor())
                .tipoDocumentoReceptor(entity.getTipoDocumentoReceptor())
                .fechaEmision(entity.getFechaEmision())
                .importeTotal(entity.getImporteTotal())
                .moneda(entity.getMoneda())
                .cae(entity.getCae())
                .caeVencimiento(entity.getCaeVencimiento())
                .estado(entity.getEstado())
                .detalle(entity.getDetalle())
                .build();
    }
}