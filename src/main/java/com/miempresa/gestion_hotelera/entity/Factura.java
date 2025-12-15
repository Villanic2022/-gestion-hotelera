package com.miempresa.gestion_hotelera.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "factura")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cliente/Hotel dueño de la factura (multi-tenant)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Reserva que origina la factura (opcional si querés facturar otras cosas)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @Column(nullable = false)
    private String tipoComprobante; // "FA", "FB", "FC", etc. o "A","B","C"

    @Column(nullable = false)
    private Integer puntoVenta;

    @Column(nullable = false)
    private Long numeroComprobante; // nro de factura

    @Column(nullable = false)
    private String cuitEmisor;

    @Column(nullable = false)
    private String documentoReceptor; // CUIT/DNI del huésped

    @Column(nullable = false)
    private String tipoDocumentoReceptor; // "CUIT", "DNI", etc

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private BigDecimal importeTotal;

    @Column(nullable = false)
    private String moneda; // "ARS"

    // Datos de AFIP/ARCA
    private String cae;
    private String caeVencimiento; // yyyy-MM-dd

    @Column(nullable = false)
    private String estado; // PENDIENTE, APROBADA, RECHAZADA

    @Column
    private String detalle; // Observaciones, descripción
}
