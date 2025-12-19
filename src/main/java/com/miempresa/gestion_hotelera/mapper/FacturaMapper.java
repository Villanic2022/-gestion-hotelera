package com.miempresa.gestion_hotelera.mapper;

import com.miempresa.gestion_hotelera.dto.FacturaResponse;
import com.miempresa.gestion_hotelera.entity.Cliente;
import com.miempresa.gestion_hotelera.entity.Factura;
import com.miempresa.gestion_hotelera.entity.Reserva;
import org.springframework.stereotype.Component;

@Component
public class FacturaMapper {

    public FacturaResponse toResponse(Factura entity) {

        Cliente cliente = entity.getCliente();   // emisor
        Reserva reserva = entity.getReserva();   // para sacar info del huésped si querés luego

        // Ahora usamos el campo receptorNombre directamente de la entidad Factura
        String receptorNombre = entity.getReceptorNombre();


        boolean esFiscal = entity.getCae() != null && !entity.getCae().isBlank();

        return FacturaResponse.builder()
                .id(entity.getId())
                .clienteId(cliente != null ? cliente.getId() : null)
                .reservaId(reserva != null ? reserva.getId() : null)

                .tipoComprobante(entity.getTipoComprobante())
                .puntoVenta(entity.getPuntoVenta())
                .numeroComprobante(entity.getNumeroComprobante())

                // Emisor
                .emisorNombre(cliente != null ? cliente.getNombre() : null)
                .emisorCuit(entity.getCuitEmisor())
                .emisorDomicilioFiscal(cliente != null ? cliente.getDomicilioFiscal() : null)

                // Receptor
                .receptorNombre(receptorNombre)
                .tipoDocumentoReceptor(entity.getTipoDocumentoReceptor())
                .documentoReceptor(entity.getDocumentoReceptor())

                .fechaEmision(entity.getFechaEmision())
                .importeTotal(entity.getImporteTotal())
                .moneda(entity.getMoneda())
                .cae(entity.getCae())
                .caeVencimiento(entity.getCaeVencimiento())
                .estado(entity.getEstado())
                .detalle(entity.getDetalle())
                .esFiscal(esFiscal)
                .build();
    }
}
