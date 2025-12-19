package com.miempresa.gestion_hotelera.service;

import com.miempresa.gestion_hotelera.dto.FacturaRequest;

import com.miempresa.gestion_hotelera.entity.Cliente;
import com.miempresa.gestion_hotelera.entity.Factura;
import com.miempresa.gestion_hotelera.entity.Reserva;
import com.miempresa.gestion_hotelera.entity.Usuario;
import com.miempresa.gestion_hotelera.repository.FacturaRepository;
import com.miempresa.gestion_hotelera.repository.ReservaRepository;
import com.miempresa.gestion_hotelera.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AfipService afipService;

    @Transactional
    public Factura emitirFacturaDesdeReserva(FacturaRequest request, String usernameActual) {

        // 1) Usuario actual (para saber cliente / tenant)
        Usuario usuario = usuarioRepository.findByUsuario(usernameActual)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        Cliente cliente = usuario.getCliente();
        if (cliente == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El usuario no tiene cliente asociado");
        }

        // 2) Reserva
        Reserva reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        if (facturaRepository.existsByReservaId(reserva.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La reserva " + reserva.getId() + " ya tiene una factura emitida"
            );
        }

        // 2.1) Importe: desde el request o desde la reserva
        BigDecimal importe = request.getImporte() != null
                ? request.getImporte()
                : reserva.getPrecioTotal(); // ya es BigDecimal

        // 3) Armamos el "request AFIP" (para futuro AFIP real)
        AfipService.AfipFacturaRequest afipReq = AfipService.AfipFacturaRequest.builder()
                .cuitEmisor(cliente.getCuit())
                .puntoVenta(request.getPuntoVenta())
                .tipoComprobante(request.getTipoComprobante())          // "B", "C", etc.
                .fecha(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)) // yyyyMMdd
                .moneda("PES")                                          // código AFIP para pesos
                .importe(importe)
                .tipoDocReceptor(mapTipoDoc(request.getTipoDocumento())) // "80"/"96"/"99"
                .nombreReceptor(request.getClienteNombre()) // 👈 AGREGAR ESTA LÍNEA
                .nroDocReceptor(request.getDocumento())
                .build();

        // 3.1) Llamar al "AFIP service"
        // AHORA: Demo (CAE y número ficticio)
        //AfipService.AfipFacturaResponse afipResp = afipService.emitirFacturaDemo(afipReq);

        // FUTURO (AFIP real):
        AfipService.AfipFacturaResponse afipResp;
        try {
            afipResp = afipService.emitirFacturaAfip(afipReq);
        } catch (Exception e) {
            // Logueamos el error y caemos en DEMO para no romper la facturación
            System.err.println("Error emitiendo factura en AFIP real: " + e.getMessage());
            e.printStackTrace();
            afipResp = afipService.emitirFacturaDemo(afipReq);
        }

        // 4) Guardar factura local
        Factura factura = Factura.builder()
                .cliente(cliente)
                .reserva(reserva)
                .tipoComprobante(request.getTipoComprobante())
                .puntoVenta(request.getPuntoVenta())
                .numeroComprobante(afipResp.getNumeroComprobante())
                .cuitEmisor(cliente.getCuit())
                .tipoDocumentoReceptor(request.getTipoDocumento())
                .documentoReceptor(request.getDocumento())
                .receptorNombre(request.getClienteNombre()) // 👈 Nuevo campo
                .fechaEmision(LocalDateTime.now())
                .importeTotal(importe)
                .moneda("ARS")
                .cae(afipResp.getCae())
                .caeVencimiento(afipResp.getCaeVencimiento())
                .estado("APROBADA") // cuando AFIP diga OK
                .detalle("Factura generada para reserva " + reserva.getId())
                .build();

        return facturaRepository.save(factura);
    }

    @Transactional(readOnly = true)
    public List<Factura> listarFacturas(
            String usernameActual,
            Long hotelId,
            LocalDate desde,
            LocalDate hasta
    ) {
        Usuario usuario = usuarioRepository.findByUsuario(usernameActual)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        Cliente cliente = usuario.getCliente();
        if (cliente == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El usuario no tiene cliente asociado");
        }

        LocalDateTime desdeDateTime = (desde != null)
                ? desde.atStartOfDay()
                : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime hastaDateTime = (hasta != null)
                ? hasta.atTime(LocalTime.of(23, 59, 59))
                : LocalDateTime.of(2100, 12, 31, 23, 59, 59);

        List<Factura> facturas = facturaRepository
                .findByClienteIdAndFechaEmisionBetweenWithFetch(
                        cliente.getId(),
                        desdeDateTime,
                        hastaDateTime
                );

        // Si querés filtrar por hotel y tu entidad Reserva tiene getHotel().getId()
        if (hotelId != null) {
            facturas = facturas.stream()
                    .filter(f -> f.getReserva() != null
                            && f.getReserva().getHotel() != null
                            && f.getReserva().getHotel().getId().equals(hotelId))
                    .toList();
        }

        return facturas;
    }

    /**
     * Obtener factura por reserva, respetando el cliente del usuario logueado.
     */
    @Transactional(readOnly = true)
    public Factura obtenerFacturaPorReserva(Long reservaId, String usernameActual) {
        Usuario usuario = usuarioRepository.findByUsuario(usernameActual)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        Cliente cliente = usuario.getCliente();
        if (cliente == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El usuario no tiene cliente asociado");
        }

        Factura factura = facturaRepository.findFirstByReservaIdWithFetch(reservaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe factura para la reserva " + reservaId));

        // Multi-tenant: chequeamos que la factura pertenezca al mismo cliente
        if (!factura.getCliente().getId().equals(cliente.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tiene acceso a la factura de esta reserva");
        }

        return factura;
    }

    /**
     * Obtener factura por ID, respetando el cliente del usuario logueado.
     */
    @Transactional(readOnly = true)
    public Factura obtenerFacturaPorId(Long facturaId, String usernameActual) {
        Usuario usuario = usuarioRepository.findByUsuario(usernameActual)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        Cliente cliente = usuario.getCliente();
        if (cliente == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El usuario no tiene cliente asociado");
        }

        Factura factura = facturaRepository.findByIdWithFetch(facturaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Factura no encontrada"));

        if (!factura.getCliente().getId().equals(cliente.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tiene acceso a esta factura");
        }

        return factura;
    }

    /**
     * Mapea el tipo de documento de tu request ("DNI", "CUIT", etc.)
     * al código que usa AFIP (por ej: 96 = DNI, 80 = CUIT).
     */
    private String mapTipoDoc(String tipo) {
        if (tipo == null) return "99"; // Otros / Consumidor final
        return switch (tipo.toUpperCase()) {
            case "CUIT" -> "80";
            case "DNI" -> "96";
            default -> "99";
        };
    }
}
