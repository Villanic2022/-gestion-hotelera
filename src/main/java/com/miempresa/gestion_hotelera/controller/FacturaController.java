package com.miempresa.gestion_hotelera.controller;

import com.miempresa.gestion_hotelera.dto.FacturaRequest;
import com.miempresa.gestion_hotelera.dto.FacturaResponse;
import com.miempresa.gestion_hotelera.entity.Factura;
import com.miempresa.gestion_hotelera.mapper.FacturaMapper;
import com.miempresa.gestion_hotelera.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;
    private final FacturaMapper facturaMapper;

    // POST /api/facturas/emitir - SOLO ADMIN
    @PostMapping("/emitir")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacturaResponse> emitirFactura(
            @RequestBody FacturaRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Factura factura = facturaService.emitirFacturaDesdeReserva(request, username);
        return ResponseEntity.ok(facturaMapper.toResponse(factura));
    }

    // GET /api/facturas?hotelId=&desde=&hasta= - SOLO ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FacturaResponse>> listarFacturas(
            Authentication authentication,
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        String username = authentication.getName();
        List<Factura> facturas = facturaService.listarFacturas(username, hotelId, desde, hasta);
        List<FacturaResponse> dtos = facturas.stream()
                .map(facturaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // GET /api/facturas/reserva/{reservaId} - SOLO ADMIN
    @GetMapping("/reserva/{reservaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacturaResponse> obtenerPorReserva(
            @PathVariable Long reservaId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Factura factura = facturaService.obtenerFacturaPorReserva(reservaId, username);
        return ResponseEntity.ok(facturaMapper.toResponse(factura));
    }

    // GET /api/facturas/{id} - SOLO ADMIN
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacturaResponse> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Factura factura = facturaService.obtenerFacturaPorId(id, username);
        return ResponseEntity.ok(facturaMapper.toResponse(factura));
    }
}
