package com.miempresa.gestion_hotelera.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    private BigDecimal monto;
    private String moneda;
    private String metodo;

    @Column(name = "pagado_por_canal")
    private Boolean pagadoPorCanal;

    @Column(name = "referencia_pago")
    private String referenciaPago;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
}
